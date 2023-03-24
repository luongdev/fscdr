package com.metechvn.freeswitchcdr.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metechvn.freeswitchcdr.messages.JsonCdrMessage;
import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.metechvn.freeswitchcdr.mongo.DynamicDocument;
import com.metechvn.freeswitchcdr.repositories.DynamicDocumentRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class JsonCdrStoreService {

    private final CollectionIdentifier identifier;
    private final DynamicDocumentRepository dynamicDocumentRepository;

    private final ObjectMapper om;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public JsonCdrStoreService(
            CollectionIdentifier identifier,
            DynamicDocumentRepository dynamicDocumentRepository,
            ObjectMapper om) {
        this.identifier = identifier;
        this.dynamicDocumentRepository = dynamicDocumentRepository;
        this.om = om;
    }

    public void store(JsonCdrMessage msg) {
        if (msg == null
                || msg.getJson() == null
                || msg.getJson().isEmpty()
                || StringUtils.isEmpty(msg.getCdrId())
                || StringUtils.isEmpty(msg.getGlobalCallId())) return;
        try {
            var variables = om.convertValue(msg.getJson().get("variables"), new TypeReference<Map<String, String>>() {
            });

            if (variables == null) {
                log.error(
                        "Cannot parse json cdr variables for cdr id: {} global call id: {}",
                        msg.getCdrId(),
                        msg.getGlobalCallId()
                );
                return;
            }

            var strStartEpoch = variables.get("start_epoch");
            var collPrefix = formatCollectionPrefix(
                    StringUtils.isEmpty(strStartEpoch)
                            ? System.currentTimeMillis()
                            : Integer.parseInt(strStartEpoch) * 1000L);

            var doc = new DynamicDocument();
            doc.put("cdrId", msg.getCdrId());
            doc.put("globalCallId", msg.getGlobalCallId());
            doc.put("json", msg.getJson());

            identifier.prefix(collPrefix).collectionName("json_cdr");

            dynamicDocumentRepository.save(doc);
        } catch (Exception e) {
            log.error(
                    "Cannot store json cdr: {} global call id: {}. Error {}",
                    msg.getCdrId(),
                    msg.getGlobalCallId(),
                    e.getMessage()
            );
        }
    }

    private String formatCollectionPrefix(long millis) {
        if (millis <= 0) return "197001";

        return new SimpleDateFormat("yyyyMM").format(new Date(millis));
    }
}
