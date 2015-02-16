/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderPendingActionId;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.util.Map;

/**
 * Created by acer on 2015/2/2.
 */
public class OrderPendingAction extends ResourceMetaForDualWrite<OrderPendingActionId> {

    /**
     * Created by acer on 2015/2/2.
     */
    public static class PropertyKey {
        public static final String balanceType = "balanceType";
    }

    private OrderPendingActionId id;

    private OrderId orderId;

    private String actionType;

    private Boolean completed;

    private Map<String, String> properties;

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public OrderPendingActionId getId() {
        return id;
    }

    @Override
    public void setId(OrderPendingActionId id) {
        this.id = id;
    }
}
