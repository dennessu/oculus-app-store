/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


import java.util.Date;
import java.util.UUID;


/**
 * subscription.
 */
@JsonPropertyOrder(value = {"trackingGuid", "subscriptionId", "userId", "offerId", "status",
        "subStartDate", "subEndDate", "paymentMethodId", "partnerId",
        "createdTime", "createdBy", "modifiedTime", "modifiedBy"})
public class Subscription extends Model {

    private UUID trackingUuid;

    //@NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subscriptionId;

    //@NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    //@NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long offerId;

    private String status;

    private Date subStartDate;

    private Date subEndDate;

    private Long paymentMethodId;

    private String partnerId;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubStartDate() {
        return status;
    }

    public void setSubStartDate(String subStartDate) {this.status = subStartDate;}


    public Long getId() {
        return subscriptionId;
    }
}
