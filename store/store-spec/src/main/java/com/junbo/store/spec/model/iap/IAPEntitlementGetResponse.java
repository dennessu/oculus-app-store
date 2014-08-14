/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.junbo.store.spec.model.Entitlement;

import java.util.List;

/**
 * The IAPEntitlementGetResponse class.
 */
public class IAPEntitlementGetResponse {

    private List<Entitlement> entitlements;

    public List<Entitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<Entitlement> entitlements) {
        this.entitlements = entitlements;
    }
}
