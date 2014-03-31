/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by Administrator on 14-3-28.
 */
@javax.persistence.Entity
@Table(name = "subscription_event")
public class SubscriptionEventEntity extends Entity {

    private Long subscriptionId;
    private Long subsEventId;
    private SubscriptionEventType eventTypeId;
    private SubscriptionStatus eventStatusId;
    private Integer retryCount;

    @Column(name = "subscription_id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Id
    @Column(name = "subs_event_id")
    public Long getSubsEventId() {
        return subsEventId;
    }

    public void setSubsEventId(Long subsEventId) {
        this.subsEventId = subsEventId;
    }

    @Column(name = "event_type_id")
    public SubscriptionEventType getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(SubscriptionEventType eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    @Column(name = "event_status_id")
    public SubscriptionStatus getEventStatusId() {
        return eventStatusId;
    }

    public void setEventStatusId(SubscriptionStatus eventStatusId) {
        this.eventStatusId = eventStatusId;
    }

    @Column(name = "retry_count")
    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    @Transient
    @Override
    public Long getId() {
        return this.subsEventId;
    }

    @Override
    public void setId(Long id) {
        this.subsEventId = id;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return subscriptionId;
    }

}
