/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.model.Results;

/**
 * The EntitlementsGetResponse class.
 */
public class EntitlementsGetResponse extends BaseResponse {

    Results<Entitlement> entitlements;

    public Results<Entitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Results<Entitlement> entitlements) {
        this.entitlements = entitlements;
    }
}
