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
@Table(name = "SUBSCRIPTION_EVENT")
public class SubscriptionEventEntity extends Entity {

    private Long subscriptionId;
    private Integer subsEventId;
    private Integer eventTypeId;
    private Integer eventStatusId;
    private Integer retryCount;

    @Id
    @Column(name = "subscription_Id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Id
    @Column(name = "subs_event_id")
    public Integer getSubsEventId() {
        return subsEventId;
    }

    public void setSubsEventId(Integer subsEventId) {
        this.subsEventId = subsEventId;
    }

    @Id
    @Column(name = "event_type_id")
    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    @Id
    @Column(name = "event_status_id")
    public Integer getEventStatusId() {
        return eventStatusId;
    }

    public void setEventStatusId(Integer eventStatusId) {
        this.eventStatusId = eventStatusId;
    }

    @Id
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
        return this.subscriptionId;
    }

    @Override
    public void setId(Long id) {
        this.subscriptionId = id;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return subscriptionId;
    }

}
