package com.metechvn.freeswitchcdr.dtos;

import java.util.ArrayList;
import java.util.List;

public class CDRSendDto {

    private final List<Entry> cdrs = new ArrayList<>();

    public List<Entry> getCdrs() {
        return cdrs;
    }

    public void setCdrs(List<Entry> cdrs) {
        this.cdrs.clear();
        if (cdrs != null) this.cdrs.addAll(cdrs);
    }

    public static class Entry {
        private String id;
        private long startTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
    }
}
