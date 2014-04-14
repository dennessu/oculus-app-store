/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.model;

/**
 * Created by Administrator on 14-3-31.
 */
public class SubscriptionEvent {
    private Long subscriptionId;

    private Long subscriptionEventId;

    private String eventType;

    private String eventStatus;

    private Integer retryCount;

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionEventId() {
        return subscriptionEventId;
    }

    public void setSubscriptionEventId(Long subscriptionEventId) {
        this.subscriptionEventId = subscriptionEventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventTypeId(String eventType) {
        this.eventType = eventType;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

}
