/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.FulfillmentEventId;
import com.junbo.common.id.OrderItemId;

import java.util.UUID;

/**
 * Created by LinYi on 2/10/14.
 */
public class FulfillmentEvent extends BaseEventModel {
    @JsonIgnore
    private FulfillmentEventId id;
    @JsonIgnore
    private OrderItemId orderItem;
    private UUID trackingUuid;
    private String fulfillmentId;

    public FulfillmentEventId getId() {
        return id;
    }

    public void setId(FulfillmentEventId id) {
        this.id = id;
    }

    public OrderItemId getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemId orderItem) {
        this.orderItem = orderItem;
    }

    @JsonIgnore
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @JsonIgnore
    public String getFulfillmentId() {
        return fulfillmentId;
    }

    public void setFulfillmentId(String fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }
}
