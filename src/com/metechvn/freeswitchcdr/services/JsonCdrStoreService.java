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

import java.util.HashMap;
import java.util.Map;

import static com.metechvn.freeswitchcdr.utils.PrefixUtils.formatCollectionPrefix;

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
                || msg.getJson().containsKey("resend")
                || StringUtils.isEmpty(msg.getCdrId())
                || StringUtils.isEmpty(msg.getGlobalCallId())) {
            if (msg != null && msg.getJson() != null && msg.getJson().containsKey("resend")) {
                log.warn("CDR {} call {} resend from log source. Ignored!", msg.getCdrId(), msg.getGlobalCallId());
            }
            return;
        }
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

            var replacements = new HashMap<String, String>();
            for (var varEntry : variables.entrySet()) {
                if (!varEntry.getKey().contains(".")) continue;

                replacements.put(varEntry.getKey(), varEntry.getKey().replace(".", "_dot_"));
            }

            if (!replacements.isEmpty()) {
                for (var repEntry : replacements.entrySet()) {
                    var value = variables.get(repEntry.getKey());

                    variables.remove(repEntry.getKey());
                    variables.put(repEntry.getValue(), value);

                    log.warn(
                            "Replaced dot key {} to {} cdr: {} global call id: {}",
                            repEntry.getKey(),
                            repEntry.getValue(),
                            msg.getCdrId(),
                            msg.getGlobalCallId()
                    );
                }

                msg.getJson().remove("variables");
                msg.getJson().put("variables", variables);
            }

            var startEpoch = StringUtils.isEmpty(variables.get("start_epoch"))
                    ? System.currentTimeMillis()
                    : Integer.parseInt(variables.get("start_epoch")) * 1000L;

            var endEpoch = StringUtils.isEmpty(variables.get("end_epoch"))
                    ? System.currentTimeMillis()
                    : Integer.parseInt(variables.get("end_epoch")) * 1000L;

            var collPrefix = formatCollectionPrefix(startEpoch);

            var doc = new DynamicDocument();
            doc.put("cdrId", msg.getCdrId());
            doc.put("globalCallId", msg.getGlobalCallId());
            doc.put("startEpoch", startEpoch);
            doc.put("endEpoch", endEpoch);
            doc.put("json", msg.getJson());
            doc.put("domainName", variables.get("domain_name"));


            var phoneNumber = variables.get("sip_h_X-Phone-Number");
            if (StringUtils.isEmpty(phoneNumber)) {
                phoneNumber = variables.get("phone_number");
            }
            doc.put("phoneNumber", phoneNumber);

            var dialedNumber = variables.get("sip_h_X-Dialed-Number");
            if (StringUtils.isEmpty(dialedNumber)) {
                dialedNumber = variables.get("dialed_number");
            }
            doc.put("dialedNumber", dialedNumber);

            var direction = variables.get("sip_h_X-Direction");
            if (StringUtils.isEmpty(direction)) {
                direction = variables.get("global_direction");
                if (StringUtils.isEmpty(direction)) {
                    direction = variables.get("direction");
                }
            }
            doc.put("direction", direction);

            identifier.prefix(collPrefix).collectionName("json_cdr");

            dynamicDocumentRepository.save(doc);
            log.debug(
                    "Saved cdr: {} global call id: {} to mongo collection {}",
                    msg.getCdrId(),
                    msg.getGlobalCallId(),
                    identifier.name()
            );
        } catch (Exception e) {
            log.error(
                    "Cannot store json cdr: {} global call id: {}. Error {}",
                    msg.getCdrId(),
                    msg.getGlobalCallId(),
                    e.getMessage()
            );
        }
    }
}
