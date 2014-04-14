/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OrderEventId;
import com.junbo.common.id.OrderId;

import java.util.UUID;

/**
 * Created by LinYi on 2/10/14.
 */
public class OrderEvent extends BaseEventModel {
    @JsonProperty("self")
    private OrderEventId id;

    private OrderId order;

    private UUID eventTrackingUuid;

    @JsonIgnore
    private String flowName;

    public OrderEventId getId() {
        return id;
    }

    public void setId(OrderEventId id) {
        this.id = id;
    }

    public OrderId getOrder() {
        return order;
    }

    public void setOrder(OrderId order) {
        this.order = order;
    }

    public UUID getEventTrackingUuid() {
        return eventTrackingUuid;
    }

    public void setEventTrackingUuid(UUID eventTrackingUuid) {
        this.eventTrackingUuid = eventTrackingUuid;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
}
