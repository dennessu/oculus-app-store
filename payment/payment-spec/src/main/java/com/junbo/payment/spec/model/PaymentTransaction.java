/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.PaymentInstrumentId;
import com.junbo.common.jackson.annotation.PaymentTransactionId;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.payment.common.FilterIn;

import java.util.List;
import java.util.UUID;

/**
 * payment transaction model.
 */
public class PaymentTransaction {
    private UUID trackingUuid;
    @UserId
    @JsonProperty("user")
    private Long userId;
    @PaymentInstrumentId
    @JsonProperty("paymentInstrument")
    private PIId paymentInstrumentId;
    private ChargeInfo chargeInfo;
    @PaymentTransactionId
    @FilterIn
    private Long id;
    @FilterIn
    private String paymentProvider;
    @FilterIn
    private String merchantAccount;
    @FilterIn
    private String status;
    @FilterIn
    private String externalToken;
    @FilterIn
    private String type;
    @FilterIn
    private List<PaymentEvent> paymentEvents;

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PIId getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    public void setPaymentInstrumentId(PIId paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    public ChargeInfo getChargeInfo() {
        return chargeInfo;
    }

    public void setChargeInfo(ChargeInfo chargeInfo) {
        this.chargeInfo = chargeInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PaymentEvent> getPaymentEvents() {
        return paymentEvents;
    }

    public void setPaymentEvents(List<PaymentEvent> paymentEvents) {
        this.paymentEvents = paymentEvents;
    }

}
