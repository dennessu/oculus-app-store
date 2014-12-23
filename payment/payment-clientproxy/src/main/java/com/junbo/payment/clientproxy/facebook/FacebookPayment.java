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
    @QueryParam("credential_id")
    @JsonProperty
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
    @QueryParam("item_type")
    @JsonProperty("item_type")
    private FacebookItemType itemType;
    @QueryParam("item_description")
    @JsonProperty("item_description")
    private FacebookItemDescription itemDescription;
    @QueryParam("refund_reason")
    @JsonProperty("refund_reason")

    private String refundReason;
    //output response
    private String id;
    private Boolean success;
    private String error;
    @JsonProperty("payment_account")
    private String paymentAccountId;
    private String application;
    @JsonProperty("time_created")
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

    public FacebookItemType getItemType() {
        return itemType;
    }

    public void setItemType(FacebookItemType itemType) {
        this.itemType = itemType;
    }

    public FacebookItemDescription getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(FacebookItemDescription itemDescription) {
        this.itemDescription = itemDescription;
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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
