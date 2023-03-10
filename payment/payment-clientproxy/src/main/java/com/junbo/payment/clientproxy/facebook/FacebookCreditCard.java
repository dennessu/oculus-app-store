/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.payment.common.CommonUtil;

import javax.ws.rs.QueryParam;

/**
 * Facebook CreditCard.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacebookCreditCard {
    private String id;
    @QueryParam("token")
    private String token;
    @QueryParam("card_holder_name")
    @JsonProperty("card_holder_name")
    private String cardHolderName;
    @QueryParam("expiry_month")
    @JsonProperty("expiry_month")
    private String expiryMonth;
    @QueryParam("expiry_year")
    @JsonProperty("expiry_year")
    private String expiryYear;
    @QueryParam("billing_address")
    @JsonProperty("billing_address")
    private FacebookAddress billingAddress;
    @QueryParam("payer_email")
    @JsonProperty("payer_email")
    private FacebookEmail payerEmail;
    @QueryParam("ip_address")
    @JsonProperty("ip_address")
    private String payerIp;
    @QueryParam("risk_features")
    @JsonProperty("risk_features")
    private FacebookRiskFeature riskFeature;

    @JsonProperty("payment_account")
    private FacebookPaymentAccount paymentAccount;
    @JsonProperty("last4")
    private String last4;
    @JsonProperty("first6")
    private String first6;
    @JsonProperty("card_type")
    private String cardType;
    @JsonProperty("last_payment_time")
    private String lastPaymentTime;
    @JsonProperty("is_enabled")
    private Boolean isEnabled;
    @JsonProperty("time_created")
    private String timeCreated;
    private FacebookCCErrorDetail error;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonIgnore
    public String getCardHolderName() {
        return cardHolderName;
    }
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }
    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public FacebookAddress getBillingAddress() {
        return billingAddress;
    }
    public void setBillingAddress(FacebookAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    @JsonIgnore
    public FacebookEmail getPayerEmail() {
        return payerEmail;
    }
    public void setPayerEmail(FacebookEmail payerEmail) {
        this.payerEmail = payerEmail;
    }

    @JsonIgnore
    public String getPayerIp() {
        return payerIp;
    }
    public void setPayerIp(String payerIp) {
        this.payerIp = payerIp;
    }

    public FacebookPaymentAccount getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(FacebookPaymentAccount paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public String getLast4() {
        return last4;
    }
    @JsonIgnore
    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getFirst6() {
        return first6;
    }
    @JsonIgnore
    public void setFirst6(String first6) {
        this.first6 = first6;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getLastPaymentTime() {
        return lastPaymentTime;
    }
    @JsonIgnore
    public void setLastPaymentTime(String lastPaymentTime) {
        this.lastPaymentTime = lastPaymentTime;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }
    @JsonIgnore
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getTimeCreated() {
        return timeCreated;
    }
    @JsonIgnore
    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public FacebookRiskFeature getRiskFeature() {
        return riskFeature;
    }

    public void setRiskFeature(FacebookRiskFeature riskFeature) {
        this.riskFeature = riskFeature;
    }

    public FacebookCCErrorDetail getError() {
        return error;
    }

    public void setError(FacebookCCErrorDetail error) {
        this.error = error;
    }

    public String toBatchString(){
        String concat = "&";
        StringBuilder sb = new StringBuilder();
        sb.append("expiry_month=" + this.expiryMonth + concat);
        sb.append("expiry_year="+ this.expiryYear + concat);
        sb.append("token=" + this.token);
        if(!CommonUtil.isNullOrEmpty(this.cardHolderName)){
            sb.append(concat + "card_holder_name=" + this.cardHolderName);
        }
        if(billingAddress != null){
            sb.append(concat + "billing_address=" + this.billingAddress.toBatchString());
        }
        if(riskFeature != null){
            sb.append(concat + "risk_features=" + this.riskFeature.toBatchString());
        }
        if(this.payerEmail != null){
            sb.append(concat + "payer_email=" + "{'address': '" + this.payerEmail.getAddress() + "'}");
        }
        if(!CommonUtil.isNullOrEmpty(this.getPayerIp())){
            sb.append(concat + "ip_address=" + this.getPayerIp());
        }
        return sb.toString();
    }
}

