/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OrderId;
import com.junbo.store.spec.model.BaseResponse;
import com.junbo.store.spec.model.Entitlement;

import java.util.List;

/**
 * The MakeFreePurchaseResponse class.
 */
public class MakeFreePurchaseResponse extends BaseResponse {

    @JsonProperty("order")
    private OrderId orderId;

    private List<Entitlement> entitlements;

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public List<Entitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<Entitlement> entitlements) {
        this.entitlements = entitlements;
    }
}
