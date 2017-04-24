package com.soso.models;

import java.util.Date;

/**
 * Created by Garik Kalashyan on 4/24/2017.
 */
public class Request {
    private Integer id;
    private Integer clientId;
    private Integer partnerId;
    private Date startTime;
    private String description;
    private Integer status;
    private String responseText;
    private Integer duration;

    public Request(){}

    public Request(Integer id, Integer clientId, Integer partnerId, Date startTime, String description, Integer status, String responseText, Integer duration) {
        this.id = id;
        this.clientId = clientId;
        this.partnerId = partnerId;
        this.startTime = startTime;
        this.description = description;
        this.status = status;
        this.responseText = responseText;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
