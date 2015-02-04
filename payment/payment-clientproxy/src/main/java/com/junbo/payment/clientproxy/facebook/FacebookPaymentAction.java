/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Facebook Payment Action.
 */
public class FacebookPaymentAction {
    @JsonProperty("request_id")
    private String requestId;
    private FacebookPaymentActionType type;
    private String status;
    private BigDecimal amount;
    private String currency;
    @JsonProperty("time_created")
    private String createdTime;
    @JsonProperty("time_updated")
    private String updatedTime;
    private FacebookRiskResult risk;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public FacebookPaymentActionType getType() {
        return type;
    }

    public void setType(FacebookPaymentActionType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public FacebookRiskResult getRisk() {
        return risk;
    }

    public void setRisk(FacebookRiskResult risk) {
        this.risk = risk;
    }

}
