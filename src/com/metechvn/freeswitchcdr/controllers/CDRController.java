package com.metechvn.freeswitchcdr.controllers;

import com.metechvn.freeswitchcdr.dtos.CDRListDto;
import com.metechvn.freeswitchcdr.dtos.PagedResult;
import com.metechvn.freeswitchcdr.messages.JsonCdrKey;
import com.metechvn.freeswitchcdr.messages.JsonCdrMessage;
import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.metechvn.freeswitchcdr.repositories.JsonCdrRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.metechvn.freeswitchcdr.utils.PrefixUtils.formatCollectionPrefix;

@RestController
@RequestMapping("/api/v1/cdr")
public class CDRController {

    private final CollectionIdentifier identifier;
    private final JsonCdrRepository jsonCdrRepository;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final int sendTimeout;
    private final String jsonCdrTopic;

    public CDRController(
            CollectionIdentifier identifier,
            JsonCdrRepository jsonCdrRepository,
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${app.json-cdr.timeout:3}") int sendTimeout,
            @Value("#{'${app.json-cdr.topic:json_cdr}'.split(':')[0]}") String jsonCdrTopic) {
        this.identifier = identifier;
        this.jsonCdrRepository = jsonCdrRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.jsonCdrTopic = jsonCdrTopic;
        this.sendTimeout = sendTimeout;
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
    public Map<String, Object> send(@RequestParam("findDate") long findDate, @RequestBody() Map<String, String[]> body) {
        if (body == null || body.isEmpty() || !body.containsKey("cdrIds"))
            return new HashMap<>() {
                {
                    put("success", false);
                    put("message", "Không có cuộc gọi được chọn");
                }
            };

        var strIds = body.get("cdrIds");
        if (strIds == null || strIds.length == 0)
            return new HashMap<>() {
                {
                    put("success", false);
                    put("message", "Không có cuộc gọi được chọn");
                }
            };

        identifier.prefix(formatCollectionPrefix(findDate)).collectionName("json_cdr");

        var uuids = new ArrayList<UUID>();
        var errors = new ArrayList<>();
        var data = new ArrayList<>();
        for (var id : strIds) {
            try {
                uuids.add(UUID.fromString(id));
            } catch (Exception ignored) {
                errors.add(id);
            }
        }

        var cdrs = jsonCdrRepository.findAllByIdIn(uuids);
        var futures = new ArrayList<Future<RecordMetadata>>();
        for (var cdr : cdrs) {
            var globalCallId = cdr.get("globalCallId", String.class);
            var uuid = cdr.get("cdrId", String.class);
            var json = cdr.get("json", Document.class);
            var id = cdr.get("_id", UUID.class);

            kafkaTemplate.execute(producer -> {
                var future = producer.send(new ProducerRecord<>(
                        jsonCdrTopic,
                        new JsonCdrKey(globalCallId),
                        new JsonCdrMessage(uuid, globalCallId, json)
                ));

                try {
                    future.get(sendTimeout, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    future.cancel(true);
                }

                return future;
            });
        }

        return new HashMap<>() {
            {
                put("success", data.size() > 0);
                put("errors", errors);
                put("data", data);
            }
        };
    }
}
