package com.metechvn.freeswitchcdr.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metechvn.freeswitchcdr.messages.JsonCdrKey;
import com.metechvn.freeswitchcdr.messages.JsonCdrMessage;
import com.metechvn.freeswitchcdr.services.JsonCdrStoreService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "true")
public class JsonCdrConsumer {

    private final JsonCdrStoreService jsonCdrStoreService;
    private final ObjectMapper om;

    public JsonCdrConsumer(JsonCdrStoreService jsonCdrStoreService, ObjectMapper om) {
        this.jsonCdrStoreService = jsonCdrStoreService;
        this.om = om;
    }

    @KafkaListener(
            groupId = "${app.instance}",
            topics = "#{'${app.json-cdr.topic:json_cdr}'.split(':')[0]}",
            containerFactory = "objListenerContainerFactory")
    public void listenJsonCdr(ConsumerRecord<JsonCdrKey, LinkedHashMap<String, Object>> msg) {
        jsonCdrStoreService.store(om.convertValue(msg.value(), JsonCdrMessage.class));
    }
}
