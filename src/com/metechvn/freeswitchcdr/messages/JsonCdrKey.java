package com.metechvn.freeswitchcdr.messages;

public class JsonCdrKey {

    private String globalCallId;

    public JsonCdrKey(String globalCallId) {
        this.globalCallId = globalCallId;
    }

    public String getGlobalCallId() {
        return globalCallId;
    }

    public void setGlobalCallId(String globalCallId) {
        this.globalCallId = globalCallId;
    }
}