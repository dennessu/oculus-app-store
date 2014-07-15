/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.store.spec.model.BaseResponse;
import com.junbo.store.spec.model.Challenge;
import com.junbo.store.spec.model.Entitlement;

import java.util.List;

/**
 * The CommitPurchaseResponse class.
 */
public class CommitPurchaseResponse extends BaseResponse {

    private Challenge challenge;

    private String orderId;

    private AppDeliveryData appDeliveryData;

    private List<Entitlement> iapEntitlements;

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
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
