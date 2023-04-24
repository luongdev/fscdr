package com.metechvn.freeswitchcdr.messages;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonCdrMessage {

    private String cdrId;
    private String globalCallId;
    private Map<String, Object> json;

    public JsonCdrMessage() {
        this.json = new HashMap<>();
    }

    public JsonCdrMessage(String cdrId, String globalCallId, Map<String, Object> json) {
        this();
        this.cdrId = cdrId;
        this.globalCallId = globalCallId;

        if (json != null) this.json.putAll(json);

        this.json.put("resend", "true");
    }

    public String getCdrId() {
        return cdrId;
    }

    public void setCdrId(String cdrId) {
        this.cdrId = cdrId;
    }

    public String getGlobalCallId() {
        return globalCallId;
    }

    public void setGlobalCallId(String globalCallId) {
        this.globalCallId = globalCallId;
    }

    public Map<String, Object> getJson() {
        return json != null ? json : Collections.emptyMap();
    }

    public void setJson(Map<String, Object> json) {
        this.json = json != null ? json : Collections.emptyMap();
    }
}
