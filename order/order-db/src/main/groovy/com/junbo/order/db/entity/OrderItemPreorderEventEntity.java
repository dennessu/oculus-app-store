/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.db.entity.enums.PreorderAction;
import com.junbo.order.db.entity.enums.EventStatus;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Created by chriszhu on 1/26/14.
 */
@Entity
@Table(name = "ORDER_ITEM_PREORDER_EVENT")
public class OrderItemPreorderEventEntity extends CommonEventEntity {
    private Long orderItemId;
    private PreorderAction action;
    private EventStatus status;

    @Column(name = "ORDER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column (name = "ACTION_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.PreorderActionType")
    public PreorderAction getAction() {
        return action;
    }

    public void setAction(PreorderAction action) {
        this.action = action;
    }

    @Column (name = "STATUS_ID")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.EventStatusType")
    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    @Override
    @Transient
    public Long getShardId() {
        return getEventId();
    }
}
