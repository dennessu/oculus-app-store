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
    private FacebookPaymentActionType type;
    private FacebookPaymentStatus status;
    private BigDecimal amount;
    private String currency;
    @JsonProperty("payer_amount")
    private BigDecimal payerAmount;
    @JsonProperty("payer_currency")
    private String payerCurrency;
    @JsonProperty("created_time")
    private String createdTime;


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

    public FacebookPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(FacebookPaymentStatus status) {
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

    public BigDecimal getPayerAmount() {
        return payerAmount;
    }

    public void setPayerAmount(BigDecimal payerAmount) {
        this.payerAmount = payerAmount;
    }

    public String getPayerCurrency() {
        return payerCurrency;
    }

    public void setPayerCurrency(String payerCurrency) {
        this.payerCurrency = payerCurrency;
    }

}
