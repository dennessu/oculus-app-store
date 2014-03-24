/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.SubscriptionId;
import com.junbo.common.jackson.annotation.UserId;


import java.util.Date;
import java.util.UUID;


/**
 * subscription.
 */

public class Subscription extends Model {

    private UUID trackingUuid;

    @JsonProperty("self")
    @SubscriptionId
    private Long subscriptionId;

    @UserId
    private Long userId;

    @OfferId
    private Long offerId;

    private String status;

    private Date subStartDate;

    private Date subEndDate;

    private Long paymentMethodId;

    private String partnerId;

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

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubStartDate() { return subStartDate; }

    public void setSubStartDate(Date subStartDate) {this.subStartDate = subStartDate;}

    public Date getSubEndDate() { return subEndDate; }

    public void setSubEndDate(Date subEndDate) {this.subEndDate = subEndDate;}

    public Long getPaymentMethodId() { return paymentMethodId; }

    public void setPaymentMethodId(Long paymentMethodId) {this.paymentMethodId = paymentMethodId;}

    public String getPartnerId() { return partnerId; }

    public void setPaymentMethodId(String partnerId) {this.partnerId = partnerId;}

    public Long getId() {
        return subscriptionId;
    }
}
