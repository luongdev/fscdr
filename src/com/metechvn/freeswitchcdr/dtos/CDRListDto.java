package com.metechvn.freeswitchcdr.dtos;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.UUID;


public class CDRListDto {

    private UUID id;
    private String cdrId;
    private String globalCallId;
    private String phoneNumber;
    private String dialedNumber;
    private String direction;
    private String domainName;
    private Long startTime;
    private Long endTime;

    public static CDRListDto of(Document doc) {
        var dto = new CDRListDto();
        if (doc == null) return dto;

        dto.id = doc.get("_id", UUID.class);
        dto.cdrId = doc.get("cdrId", String.class);
        dto.globalCallId = doc.get("globalCallId", String.class);
        dto.startTime = doc.get("startEpoch", Long.class);
        dto.endTime = doc.get("endEpoch", Long.class);
        dto.domainName = doc.get("domainName", String.class);

        var json = doc.get("json", Document.class);
        if (json == null || !json.containsKey("variables")) return dto;

        var variables = json.get("variables", Document.class);
        if (variables == null) return dto;

        if (StringUtils.isEmpty(dto.domainName)) {
            dto.domainName = String.valueOf(variables.get("domain_name"));
        }

        dto.phoneNumber = variables.get("sip_h_X-Phone-Number", String.class);
        if (StringUtils.isEmpty(dto.phoneNumber)) {
            dto.phoneNumber = variables.get("phone_number", String.class);
        }

        dto.dialedNumber = variables.get("sip_h_X-Dialed-Number", String.class);
        if (StringUtils.isEmpty(dto.dialedNumber)) {
            dto.dialedNumber = variables.get("dialed_number", String.class);
        }

        dto.direction = variables.get("sip_h_X-Direction", String.class);
        if (StringUtils.isEmpty(dto.direction)) {
            dto.direction = variables.get("global_direction", String.class);
            if (StringUtils.isEmpty(dto.direction)) {
                dto.direction = variables.get("direction", String.class);
            }
        }

        return dto;
    }

    @Override
    public String toString() {
        return "CDRListDto{" +
                "id=" + id +
                ", cdrId='" + cdrId + '\'' +
                ", globalCallId='" + globalCallId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dialedNumber='" + dialedNumber + '\'' +
                ", direction='" + direction + '\'' +
                ", domainName='" + domainName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
