/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.util.UUID;

/**
 * Created by LinYi on 2/10/14.
 */
public class FulfillmentHistory extends ResourceMetaForDualWrite<Long> {
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long orderItemId;

    @JsonIgnore
    private UUID trackingUuid;

    @JsonIgnore
    private String fulfillmentId;

    private String fulfillmentEvent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public String getFulfillmentId() {
        return fulfillmentId;
    }

    public void setFulfillmentId(String fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public String getFulfillmentEvent() {
        return fulfillmentEvent;
    }

    public void setFulfillmentEvent(String fulfillmentEvent) {
        this.fulfillmentEvent = fulfillmentEvent;
    }
}
