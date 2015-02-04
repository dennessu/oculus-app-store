/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.payment.common.CommonUtil;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.List;

/**
 * Facebook Payment.
 */
public class FacebookPayment {
    //input request
    @QueryParam("request_id")
    @JsonProperty("request_id")
    private String requestId;
    @QueryParam("credential_id")
    @JsonProperty("credential_id")
    private String credentialId;
    @QueryParam("amount")
    private BigDecimal amount;
    @QueryParam("currency")
    private String currency;
    @QueryParam("action")
    private FacebookPaymentActionType action;
    @QueryParam("ip_address")
    @JsonProperty("ip_address")
    private String payerIp;
    @QueryParam("payment_type")
    @JsonProperty("payment_type")
    private FacebookItemType itemType;
    @QueryParam("payment_description")
    @JsonProperty("payment_description")
    private FacebookItemDescription itemDescription;
    @QueryParam("refund_reason")
    @JsonProperty("refund_reason")
    private String refundReason;
    @QueryParam("risk_features")
    @JsonProperty("risk_features")
    private FacebookRiskFeature riskFeature;
    //output response
    private String id;
    private Boolean success;
    private FacebookCCErrorDetail error;
    @JsonProperty("payment_account")
    @JsonIgnore
    private String paymentAccountId;
    @JsonIgnore
    private String application;
    @JsonProperty("time_created")
    private String createdTime;
    private List<FacebookPaymentAction> actions;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
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

    public FacebookCCErrorDetail getError() {
        return error;
    }

    public void setError(FacebookCCErrorDetail error) {
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

    public FacebookRiskFeature getRiskFeature() {
        return riskFeature;
    }

    public void setRiskFeature(FacebookRiskFeature riskFeature) {
        this.riskFeature = riskFeature;
    }


    public String toBatchString(){
        String concat = "&";
        StringBuilder sb = new StringBuilder();
        sb.append("credential_id="+ this.credentialId + concat);
        sb.append("action=" + this.action.toString() + concat);
        sb.append("amount=" + this.amount + concat);
        sb.append("currency=" + this.currency + concat);
        sb.append("payment_type=" + this.itemType + concat);
        if(!CommonUtil.isNullOrEmpty(this.requestId)){
            sb.append(concat + "request_id=" + this.requestId);
        }
        if(!CommonUtil.isNullOrEmpty(this.getPayerIp())){
            sb.append(concat + "ip_address=" + this.getPayerIp());
        }
        if(this.getItemDescription() != null){
            sb.append(concat + "payment_description=" + this.itemDescription.toBatchString());
        }
        if(riskFeature != null){
            sb.append(concat + "risk_features=" + this.riskFeature.toBatchString());
        }

        return sb.toString();
    }

}
