/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.EventStatus;
import com.junbo.order.spec.model.OrderActionType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by chriszhu on 1/26/14.
 */

@Entity
@Table(name = "ORDER_EVENT")
public class OrderEventEntity extends CommonEventEntity {
    private Long orderId;
    private OrderActionType actionId;
    private EventStatus statusId;
    private UUID trackingUuid;
    private UUID flowName;

    @Column(name = "ORDER_ID")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column (name = "ACTION_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public OrderActionType getActionId() {
        return actionId;
    }

    public void setActionId(OrderActionType action) {
        this.actionId = action;
    }

    @Column (name = "STATUS_ID")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    public EventStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(EventStatus statusId) {
        this.statusId = statusId;
    }

    @Column (name = "TRACKING_UUID")
    @Type(type = "pg-uuid")
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @Column (name = "FLOW_NAME")
    @Type(type = "pg-uuid")
    public UUID getFlowName() {
        return flowName;
    }

    public void setFlowName(UUID flowName) {
        this.flowName = flowName;
    }
}
