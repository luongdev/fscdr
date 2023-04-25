package com.metechvn.freeswitchcdr.projection;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

public class CDRListProjection {

    @BsonId
    private String id;
    private String cdrId;
    private String globalCallId;
    private String phoneNumber;
    private String dialedNumber;
    private String direction;
    private String domainName;
    private Long startEpoch;
    private Long endEpoch;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDialedNumber() {
        return dialedNumber;
    }

    public void setDialedNumber(String dialedNumber) {
        this.dialedNumber = dialedNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(Long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public Long getEndEpoch() {
        return endEpoch;
    }

    public void setEndEpoch(Long endEpoch) {
        this.endEpoch = endEpoch;
    }
}
