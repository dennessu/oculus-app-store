/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.spec.model.Entitlement;

/**
 * EntitlementAuthorizeCallback.
 */
public class EntitlementAuthorizeCallback extends AbstractAuthorizeCallback<Entitlement> {
    public EntitlementAuthorizeCallback(EntitlementAuthorizeCallbackFactory factory, Entitlement entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "entitlements";
    }

    @Override
    protected UserId getUserOwnerId() {
        Entitlement entity = getEntity();
        if (entity != null) {
            return new UserId(entity.getUserId());
        }

        return null;
    }
}
