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

    private AppDeliveryData appDeliveryData;

    private List<Entitlement> iapEntitlements;

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

    public AppDeliveryData getAppDeliveryData() {
        return appDeliveryData;
    }

    public void setAppDeliveryData(AppDeliveryData appDeliveryData) {
        this.appDeliveryData = appDeliveryData;
    }

    public List<Entitlement> getIapEntitlements() {
        return iapEntitlements;
    }

    public void setIapEntitlements(List<Entitlement> iapEntitlements) {
        this.iapEntitlements = iapEntitlements;
    }
}
