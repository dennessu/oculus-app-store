/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.db.entity.enums.BillingAction;
import com.junbo.order.db.entity.enums.EventStatus;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Created by chriszhu on 1/26/14.
 */
@Entity
@Table(name= "ORDER_BILLING_EVENT")
public class OrderBillingEventEntity extends CommonEventEntity{
    private Long orderId;
    private String balanceId;
    private BillingAction action;
    private EventStatus status;

    @Column(name = "ORDER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "BALANCE_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    @Column (name = "ACTION_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.BillingActionType")
    public BillingAction getAction() {
        return action;
    }

    public void setAction(BillingAction action) {
        this.action = action;
    }

    @Column (name = "STATUS_ID")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.EventStatusType")
    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus statusId) {
        this.status = statusId;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderId;
    }
}
