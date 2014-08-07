/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.common.id.OrderId;
import com.junbo.store.spec.model.Entitlement;

import java.util.List;

/**
 * The MakeFreePurchaseResponse class.
 */
public class MakeFreePurchaseResponse {

    private OrderId order;

    private List<Entitlement> entitlements;

    public OrderId getOrder() {
        return order;
    }

    public void setOrder(OrderId order) {
        this.order = order;
    }

    public List<Entitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<Entitlement> entitlements) {
        this.entitlements = entitlements;
    }
}
