/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Facebook CreditCard.
 */
public class FacebookCreditCard {
    private String id;
    @JsonProperty("cc_number")
    private String ccNumber;
    @JsonProperty
    private String cvv;
    @JsonProperty("card_holder_name")
    private String cardHolderName;
    @JsonProperty("expiry_month")
    private String expiryMonth;
    @JsonProperty("expiry_year")
    private String expiryYear;
    @JsonProperty("billing_address")
    private FacebookAddress billingAddress;
    @JsonProperty("payer_email")
    private String payerEmail;
    @JsonProperty("payer_ip")
    private String payerIp;

    @JsonProperty("payment_account")
    private String paymentAccountId;
    @JsonProperty
    private String last4;
    @JsonProperty
    private String first6;
    @JsonProperty("created_time")
    private String createdTime;
    @JsonProperty("last_payment_time")
    private String lastPaymentTime;
    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getCcNumber() {
        return ccNumber;
    }
    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    @JsonIgnore
    public String getCvv() {
        return cvv;
    }
    public void setCvv(String cvv) {
        this.cvv = cvv;
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
    public String getPayerEmail() {
        return payerEmail;
    }
    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    @JsonIgnore
    public String getPayerIp() {
        return payerIp;
    }
    public void setPayerIp(String payerIp) {
        this.payerIp = payerIp;
    }

    public String getPaymentAccountId() {
        return paymentAccountId;
    }
    @JsonIgnore
    public void setPaymentAccountId(String paymentAccountId) {
        this.paymentAccountId = paymentAccountId;
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

    public String getCreatedTime() {
        return createdTime;
    }
    @JsonIgnore
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
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
}

