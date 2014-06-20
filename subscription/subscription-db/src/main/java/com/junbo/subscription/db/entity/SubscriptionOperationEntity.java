/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.entity;

import com.junbo.common.util.Identifiable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by Administrator on 14-6-20.
 */
@javax.persistence.Entity
@Table(name = "subscription_operation")
public class SubscriptionOperationEntity  extends Entity  implements Identifiable<Long> {

    private Long subsOperationId;
    private Long subscriptionId;
    private SubscriptionEventType eventTypeId;
    private SubscriptionStatus operationStatusId;
    private Date operationScheduleDate;
    private boolean retryable;
    private int retryCount;

    @Id
    @Column(name = "subs_operation_id")
    public Long getSubsOperationId() {
        return subsOperationId;
    }

    public void setSubsOperationId(Long subsOperationId) {
        this.subsOperationId = subsOperationId;
    }

    @Column(name = "subscription_id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Column(name = "event_type_id")
    public SubscriptionEventType getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(SubscriptionEventType eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    @Column(name = "operation_status_id")
    public SubscriptionStatus getOperationStatusId() {
        return operationStatusId;
    }

    public void setOperationStatusId(SubscriptionStatus operationStatusId) {
        this.operationStatusId = operationStatusId;
    }

    @Column(name = "operation_schedule_date")
    public Date getOperationScheduleDate() {
        return operationScheduleDate;
    }

    public void setOperationScheduleDate(Date operationScheduleDate) {
        this.operationScheduleDate = operationScheduleDate;
    }

    @Column(name = "retryable")
    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    @Column(name = "retry_count")
    public int getRetryCount() {
        return retryCount;
    }

    @Column(name = "subscription_id")
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Transient
    @Override
    public Long getId() {
        return this.subsOperationId;
    }

    @Override
    public void setId(Long subsOperationId) {
        this.subsOperationId = subsOperationId;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return subscriptionId;
    }

}
