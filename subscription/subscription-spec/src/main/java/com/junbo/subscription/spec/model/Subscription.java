/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.CountryId;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.SubscriptionId;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.model.ResourceMeta;


import java.util.Date;
import java.util.UUID;


/**
 * subscription.
 */

public class Subscription extends ResourceMeta<Long> {

    private UUID trackingUuid;

    @JsonProperty("self")
    @SubscriptionId
    private Long subscriptionId;

    @UserId
    private Long userId;

    @OfferId
    private String offerId;

    private String status;

    private Date subsStartDate;

    private Date subsEndDate;

    private Long paymentMethodId;

    private String source;

    private Integer anniversaryDay;

    @CountryId
    private String country;

    @CurrencyId
    private String currency;

    public Subscription() {
    }

    public UUID getTrackingUuid() {return trackingUuid;}

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubsStartDate() { return subsStartDate; }

    public void setSubsStartDate(Date subsStartDate) {this.subsStartDate = subsStartDate;}

    public Date getSubsEndDate() { return subsEndDate; }

    public void setSubsEndDate(Date subsEndDate) {this.subsEndDate = subsEndDate;}

    public Long getPaymentMethodId() { return paymentMethodId; }

    public void setPaymentMethodId(Long paymentMethodId) {this.paymentMethodId = paymentMethodId;}

    public String getSource() { return source; }

    public void setSource(String source) {this.source = source;}

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonIgnore
    public Long getId() {
        return subscriptionId;
    }

    @Override
    public void setId(Long id) {
        this.subscriptionId = id;
    }

    public Integer getAnniversaryDay() { return anniversaryDay; }

    public void setAnniversaryDay(Integer anniversaryDay) { this.anniversaryDay = anniversaryDay; }
}
