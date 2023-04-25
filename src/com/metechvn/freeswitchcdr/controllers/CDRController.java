package com.metechvn.freeswitchcdr.controllers;

import com.metechvn.freeswitchcdr.dtos.CDRListDto;
import com.metechvn.freeswitchcdr.dtos.CDRSendDto;
import com.metechvn.freeswitchcdr.dtos.CDRUpdateDomainDto;
import com.metechvn.freeswitchcdr.dtos.PagedResult;
import com.metechvn.freeswitchcdr.messages.JsonCdrKey;
import com.metechvn.freeswitchcdr.messages.JsonCdrMessage;
import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.metechvn.freeswitchcdr.repositories.MongoTemplateRepository;
import com.mongodb.client.model.UpdateOptions;
import io.micrometer.common.util.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ExecutorService executorService;
    private final Lock prefixLock;
    private final int sendTimeout;
    private final String jsonCdrTopic;

    @Autowired
    MongoTemplateRepository mongoTemplateRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CDRController(
            CollectionIdentifier identifier,
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Qualifier("prefixLock") Lock prefixLock,
            @Value("${app.json-cdr.timeout:3}") int sendTimeout,
            @Value("#{'${app.json-cdr.topic:json_cdr}'.split(':')[0]}") String jsonCdrTopic) {
        this.identifier = identifier;
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
        var pageable = PageRequest.of(page, size, Sort.by("startEpoch").ascending());
        var pagedResult = mongoTemplateRepository.query(keyword, fromDate, toDate, pageable);

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

    @PostMapping({"change-domain", "/change-domain"})
    public Map<String, Object> updateDomain(@RequestBody() CDRUpdateDomainDto body) {
        if (body == null
                || body.getStartTime() <= 0
                || StringUtils.isEmpty(body.getId())
                || StringUtils.isEmpty(body.getDomainName()))
            return new HashMap<>() {
                {
                    put("success", false);
                    put("message", "Không có cuộc gọi được chọn");
                }
            };


        var result = identifier
                .prefix(formatCollectionPrefix(body.getStartTime()))
                .collectionName("json_cdr")
                .collection()
                .updateOne(
                        new Document("_id", body.getId()),
                        new Document("$set", new Document(
                                Map.of(
                                        "domainName", body.getDomainName(),
                                        "json.variables.domain_name", body.getDomainName()
                                )
                        )),
                        new UpdateOptions().upsert(true)
                );

        return new HashMap<>() {
            {
                put("success", result.getModifiedCount() > 0);
                put("data", result);
            }
        };
    }

    private Callable<SendResult> sendTask(String prefix, List<CDRSendDto.Entry> entries) {
        if (StringUtils.isEmpty(prefix) || entries == null || entries.isEmpty()) return SendResult::new;

        return () -> {
            var start = System.currentTimeMillis();
            var result = new SendResult();

            var ids = entries.stream().map(CDRSendDto.Entry::getId).toList();


            List<Document> cdrs;
            prefixLock.lock();
            try {
                log.debug("Lock resource(s) to waiting select cdr!");
                cdrs = identifier.collectionName("json_cdr").prefix(prefix).template().find(
                        new Query(Criteria.where("_id").in(ids)),
                        Document.class,
                        identifier.name()
                );
                log.debug("Found {} cdr(s) with prefix {}", cdrs.size(), prefix);
            } finally {
                prefixLock.unlock();
                log.debug("Unlock resource(s)!");
            }

            if (cdrs.size() == 0) return result;

            for (var cdr : cdrs) {
                var globalCallId = cdr.get("globalCallId", String.class);
                var uuid = cdr.get("cdrId", String.class);
                var json = cdr.get("json", Document.class);
                var id = cdr.get("_id", String.class);

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
        private final List<String> successIds = new ArrayList<>();
        private final List<String> errorIds = new ArrayList<>();

        public void success(String... ids) {
            success(List.of(ids));
        }

        public void success(Collection<String> ids) {
            if (ids == null) return;

            this.successIds.addAll(ids);
        }

        public void error(String... ids) {
            error(List.of((ids)));
        }

        public void error(Collection<String> ids) {
            if (ids == null) return;

            this.errorIds.addAll(ids);
        }

        public List<String> getSuccessIds() {
            return successIds;
        }

        public List<String> getErrorIds() {
            return errorIds;
        }
    }
}
