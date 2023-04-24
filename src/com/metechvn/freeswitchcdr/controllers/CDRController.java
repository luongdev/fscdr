package com.metechvn.freeswitchcdr.controllers;

import com.metechvn.freeswitchcdr.dtos.CDRListDto;
import com.metechvn.freeswitchcdr.dtos.CDRSendDto;
import com.metechvn.freeswitchcdr.dtos.PagedResult;
import com.metechvn.freeswitchcdr.messages.JsonCdrKey;
import com.metechvn.freeswitchcdr.messages.JsonCdrMessage;
import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.metechvn.freeswitchcdr.repositories.JsonCdrRepository;
import io.micrometer.common.util.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static com.metechvn.freeswitchcdr.utils.PrefixUtils.formatCollectionPrefix;

@RestController
@RequestMapping("/api/v1/cdr")
public class CDRController {

    private final CollectionIdentifier identifier;
    private final JsonCdrRepository jsonCdrRepository;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ExecutorService executorService;
    private final Lock prefixLock;
    private final int sendTimeout;
    private final String jsonCdrTopic;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CDRController(
            CollectionIdentifier identifier,
            JsonCdrRepository jsonCdrRepository,
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Qualifier("prefixLock") Lock prefixLock,
            @Value("${app.json-cdr.timeout:3}") int sendTimeout,
            @Value("#{'${app.json-cdr.topic:json_cdr}'.split(':')[0]}") String jsonCdrTopic) {
        this.identifier = identifier;
        this.jsonCdrRepository = jsonCdrRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.prefixLock = prefixLock;
        this.jsonCdrTopic = jsonCdrTopic;
        this.sendTimeout = sendTimeout;
        this.executorService = Executors.newFixedThreadPool(8);
    }

    @GetMapping({"", "/"})
    public PagedResult get(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("fromDate") long fromDate,
            @RequestParam(name = "toDate", required = false) Long toDate,
            @RequestParam(name = "keyword", required = false) String keyword) {
        identifier.prefix(formatCollectionPrefix(fromDate)).collectionName("json_cdr");

        var pageable = PageRequest.of(page, size, Sort.by("startEpoch").ascending());
        Page<Document> pagedResult;
        if (toDate == null || toDate < fromDate) {
            pagedResult = jsonCdrRepository.findBy(keyword, fromDate, pageable);
        } else {
            pagedResult = jsonCdrRepository.findBy(keyword, fromDate, toDate, pageable);
        }

        return PagedResult.of(
                pagedResult.stream().map(CDRListDto::of).toList(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages()
        );
    }

    @PostMapping({"send", "/send"})
    public Map<String, Object> send(@RequestBody() CDRSendDto body) throws InterruptedException, ExecutionException {
        if (body == null || body.getCdrs().isEmpty())
            return new HashMap<>() {
                {
                    put("success", false);
                    put("message", "Không có cuộc gọi được chọn");
                }
            };

        var groupedCDRs = new HashMap<String, List<CDRSendDto.Entry>>();
        for (var entry : body.getCdrs()) {
            var prefix = formatCollectionPrefix(entry.getStartTime());
            if (!groupedCDRs.containsKey(prefix)) groupedCDRs.put(prefix, new ArrayList<>());

            groupedCDRs.get(prefix).add(entry);
        }

        var callables = new ArrayList<Future<SendResult>>();
        for (var entry : groupedCDRs.entrySet()) {
            callables.add(executorService.submit(this.sendTask(entry.getKey(), entry.getValue())));
        }

        var sentResult = new SendResult();
        for (var callable : callables) {
            var result = callable.get();
            sentResult.success(result.successIds);
            sentResult.error(result.errorIds);
        }

        return new HashMap<>() {
            {
                put("success", sentResult.successIds.size() > 0);
                put("data", sentResult);
            }
        };
    }

    private Callable<SendResult> sendTask(String prefix, List<CDRSendDto.Entry> entries) {
        if (StringUtils.isEmpty(prefix) || entries == null || entries.isEmpty()) return SendResult::new;

        return () -> {
            var start = System.currentTimeMillis();
            var result = new SendResult();

            List<Document> cdrs;
            prefixLock.lock();
            try {
                log.debug("Lock resource(s) to waiting select cdr!");
                cdrs = jsonCdrRepository.findAllByIdIn(entries.stream().map(CDRSendDto.Entry::getId).toList());
                log.debug("Found {} cdr(s) with prefix {}", cdrs == null ? 0 : cdrs.size(), prefix);
            } finally {
                prefixLock.unlock();
                log.debug("Unlock resource(s)!");
            }

            if (cdrs == null || cdrs.size() == 0) return result;

            for (var cdr : cdrs) {
                var globalCallId = cdr.get("globalCallId", String.class);
                var uuid = cdr.get("cdrId", String.class);
                var json = cdr.get("json", Document.class);
                var id = cdr.get("_id", UUID.class);

                try {
                    kafkaTemplate.send(new ProducerRecord<>(
                            jsonCdrTopic,
                            new JsonCdrKey(globalCallId),
                            new JsonCdrMessage(uuid, globalCallId, json)
                    )).get(sendTimeout, TimeUnit.SECONDS);
                    result.success(id);

                    log.debug(
                            "Sent cdr {}({}) call {} to topic {} using timeout {} second(s)",
                            id, uuid, globalCallId, jsonCdrTopic, sendTimeout
                    );
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    result.error(id);
                    log.error(
                            "Cannot send cdr {}({}) call {} to topic {} using timeout {} second(s). Trace: {}",
                            id, uuid, globalCallId, jsonCdrTopic, sendTimeout, e.getMessage()
                    );
                }
            }

            log.debug("Process cdr prefix {} take {}. Success: {}, error: {}",
                    prefix, System.currentTimeMillis() - start, result.successIds.size(), result.errorIds.size()
            );

            return result;
        };
    }

    static class SendResult {
        private final List<UUID> successIds = new ArrayList<>();
        private final List<UUID> errorIds = new ArrayList<>();

        public void success(UUID... ids) {
            success(List.of(ids));
        }

        public void success(Collection<UUID> ids) {
            if (ids == null) return;

            this.successIds.addAll(ids);
        }

        public void error(UUID... ids) {
            error(List.of((ids)));
        }

        public void error(Collection<UUID> ids) {
            if (ids == null) return;

            this.errorIds.addAll(ids);
        }

        public List<UUID> getSuccessIds() {
            return successIds;
        }

        public List<UUID> getErrorIds() {
            return errorIds;
        }
    }
}
