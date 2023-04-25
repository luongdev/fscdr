package com.metechvn.freeswitchcdr.dtos;

import com.metechvn.freeswitchcdr.projection.CDRListProjection;


public class CDRListDto {

    private String id;
    private String cdrId;
    private String globalCallId;
    private String phoneNumber;
    private String dialedNumber;
    private String direction;
    private String domainName;
    private Long startTime;
    private Long endTime;

    public static CDRListDto of(CDRListProjection doc) {
        var dto = new CDRListDto();
        if (doc == null) return dto;

        dto.id = doc.getId();
        dto.cdrId = doc.getCdrId();
        dto.globalCallId = doc.getGlobalCallId();
        dto.startTime = doc.getStartEpoch();
        dto.endTime = doc.getEndEpoch();
        dto.domainName = doc.getDomainName();
        dto.phoneNumber = doc.getPhoneNumber();
        dto.dialedNumber = doc.getDialedNumber();
        dto.direction = doc.getDirection();

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
