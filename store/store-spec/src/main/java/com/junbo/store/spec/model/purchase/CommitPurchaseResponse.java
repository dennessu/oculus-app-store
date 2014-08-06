/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.common.id.OrderId;
import com.junbo.store.spec.model.BaseResponse;
import com.junbo.store.spec.model.Challenge;
import com.junbo.store.spec.model.Entitlement;

import java.util.List;

/**
 * The CommitPurchaseResponse class.
 */
public class CommitPurchaseResponse extends BaseResponse {

    private Challenge challenge;

    private OrderId orderId;

    private List<Entitlement> entitlements;

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

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
