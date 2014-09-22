/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.List;

/**
 * Facebook Payment.
 */
public class FacebookPayment {
    //input request
    @QueryParam("credential")
    private String credential;
    @QueryParam("amount")
    private BigDecimal amount;
    @QueryParam("currency")
    private String currency;
    @QueryParam("action")
    private FacebookPaymentActionType action;
    @QueryParam("payer_ip")
    @JsonProperty("payer_ip")
    private String payerIp;
    @QueryParam("payment_description")
    @JsonProperty("payment_description")
    private String paymentDescription;
    @QueryParam("refund_reason")
    @JsonProperty("refund_reason")
    private String refundReason;
    //output response
    private String id;
    @JsonProperty("payment_account")
    private String paymentAccountId;
    private String application;
    @JsonProperty("created_time")
    private String createdTime;
    private List<FacebookPaymentAction> actions;

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
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

    public FacebookPaymentActionType getAction() {
        return action;
    }

    public void setAction(FacebookPaymentActionType action) {
        this.action = action;
    }

    public String getPayerIp() {
        return payerIp;
    }

    public void setPayerIp(String payerIp) {
        this.payerIp = payerIp;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public void setPaymentDescription(String paymentDescription) {
        this.paymentDescription = paymentDescription;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentAccountId() {
        return paymentAccountId;
    }

    public void setPaymentAccountId(String paymentAccountId) {
        this.paymentAccountId = paymentAccountId;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public List<FacebookPaymentAction> getActions() {
        return actions;
    }

    public void setActions(List<FacebookPaymentAction> actions) {
        this.actions = actions;
    }

}
