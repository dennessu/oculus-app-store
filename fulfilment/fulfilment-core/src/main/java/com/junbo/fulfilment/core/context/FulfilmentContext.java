/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.context;

import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;

import java.util.List;
import java.util.Map;

/**
 * FulfilmentContext.
 */
public abstract class FulfilmentContext {
    protected Long userId;
    protected Long orderId;
    protected List<FulfilmentAction> actions;
    protected Map<Long, FulfilmentItem> items;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<FulfilmentAction> getActions() {
        return actions;
    }

    public void setActions(List<FulfilmentAction> actions) {
        this.actions = actions;
    }

    public Map<Long, FulfilmentItem> getItems() {
        return items;
    }

    public void setItems(Map<Long, FulfilmentItem> items) {
        this.items = items;
    }
}
