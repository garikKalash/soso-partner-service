package com.soso.models;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Garik Kalashyan on 4/15/2017.
 */
public class Feedback{
    private Integer id;
    private String context;
    private BigDecimal rate;
    private Integer partnerId;
    private Integer clientId;


    public Feedback(Integer id, String context, BigDecimal rate, Integer partnerId, Integer clientId) {
        this.id = id;
        this.context = context;
        this.rate = rate;
        this.partnerId = partnerId;
        this.clientId = clientId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }



}
