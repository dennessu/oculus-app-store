/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.OrderPendingActionType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by fzhang on 2015/2/2.
 */
@Entity
@Table(name = "ORDER_PENDING_ACTION")
public class OrderPendingActionEntity extends CommonDbEntityWithDate {

    private Long pendingActionId;
    private OrderPendingActionType actionType;
    private Long orderId;
    private Boolean completed;
    private String properties;

    @Id
    @Column(name = "PENDING_ACTION_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getPendingActionId() {
        return pendingActionId;
    }

    public void setPendingActionId(Long pendingActionId) {
        this.pendingActionId = pendingActionId;
    }

    @Column(name = "ACTION_TYPE")
    @Type(type = "com.junbo.order.db.entity.type.OrderPendingActionEnumType")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public OrderPendingActionType getActionType() {
        return actionType;
    }

    public void setActionType(OrderPendingActionType actionType) {
        this.actionType = actionType;
    }

    @Column(name = "ORDER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "IS_COMPLETED")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Column(name = "PROPERTIES")
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    @Transient
    public Long getShardId() {
        return pendingActionId;
    }
}
