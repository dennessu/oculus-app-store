/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.junbo.common.model.Results;
import com.junbo.store.spec.model.Entitlement;

/**
 * The IAPEntitlementGetResponse class.
 */
public class IAPEntitlementGetResponse {

    private Results<Entitlement> entitlements;

    public Results<Entitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Results<Entitlement> entitlements) {
        this.entitlements = entitlements;
    }
}