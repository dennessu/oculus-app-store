/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

/**
 * The IAPEntitlementConsumeRequest class.
 */
public class IAPConsumeItemRequest {

    private String iapPurchaseToken;

    public String getIapPurchaseToken() {
        return iapPurchaseToken;
    }

    public void setIapPurchaseToken(String iapPurchaseToken) {
        this.iapPurchaseToken = iapPurchaseToken;
    }
}
