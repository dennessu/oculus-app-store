/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.FulfillmentEventType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by chriszhu on 1/26/14.
 */
@Entity
@Table(name = "ORDER_ITEM_FULFILLMENT_HISTORY")
public class OrderItemFulfillmentHistoryEntity extends CommonDbEntityWithDate {
    private Long historyId;
    private Long orderItemId;
    private UUID trackingUuid;
    private String fulfillmentId;
    private FulfillmentEventType fulfillmentEventId;
    private Boolean success;

    @Id
    @Column(name = "HISTORY_ID")
    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    @Column(name = "ORDER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "TRACKING_UUID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "pg-uuid")
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @Column(name = "FULFILLMENT_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getFulfillmentId() {
        return fulfillmentId;
    }

    public void setFulfillmentId(String fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    @Column (name = "FULFILLMENT_EVENT_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.FulfillmentActionType")
    public FulfillmentEventType getFulfillmentEventId() {
        return fulfillmentEventId;
    }

    public void setFulfillmentEventId(FulfillmentEventType fulfillmentEventId) {
        this.fulfillmentEventId = fulfillmentEventId;
    }

    @Override
    @Transient
    public Long getShardId() {
        return getHistoryId();
    }

    @Column(name = "SUCCESS")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
