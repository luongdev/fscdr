package com.metechvn.jsoncdr.consumers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metechvn.es.EsIndexIdentifier;
import com.metechvn.jsoncdr.entities.JsonCdr;
import com.metechvn.jsoncdr.messages.JsonCdrKey;
import com.metechvn.jsoncdr.repositories.es.JsonCdrEsRepository;
import com.metechvn.utils.Utils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JsonCdrConsumer {

    private final EsIndexIdentifier identifier;
    private final JsonCdrEsRepository jsonCdrEsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonCdrConsumer(EsIndexIdentifier identifier, JsonCdrEsRepository jsonCdrEsRepository) {
        this.identifier = identifier;
        this.jsonCdrEsRepository = jsonCdrEsRepository;
    }

    @KafkaListener(
            groupId = "${spring.kafka.client-id:FS-CDR}",
            topics = "#{'${app.json-cdr.topic:json_cdr}'.split(':')[0]}",
            containerFactory = "objListenerContainerFactory")
    public void listenJsonCdr(ConsumerRecord<JsonCdrKey, LinkedHashMap<String, Object>> msg) {
        if (msg == null || msg.value() == null || msg.value().isEmpty()) return;

        try {
            var id = UUID.fromString(String.valueOf(msg.value().get("cdrId")));

            var json = objectMapper.convertValue(msg.value().get("json"), new TypeReference<Map<String, Object>>() {
            });
            var variables = objectMapper.convertValue(json.get("variables"), new TypeReference<Map<String, Object>>() {
            });

            json.remove("variables");

            var jsonCdr = new JsonCdr();
            jsonCdr.setId(id);
            json.put("global_call_id", msg.value().get("globalCallId"));

            for (var entry : json.entrySet())
                jsonCdr.put(Utils.camelCaseToUnderscore(entry.getKey()), entry.getValue());

            jsonCdr.putAll(variables);
            jsonCdr.put("start_epoch", Long.parseLong(String.valueOf(variables.get("start_epoch"))));

            final var monthStr = Utils.formatDate(new Date(jsonCdr.getStartedEpoch() * 1000), "yyyy_MM");
            identifier.setIndexName("json_cdr").setPrefix(monthStr);

            jsonCdrEsRepository.save(jsonCdr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
