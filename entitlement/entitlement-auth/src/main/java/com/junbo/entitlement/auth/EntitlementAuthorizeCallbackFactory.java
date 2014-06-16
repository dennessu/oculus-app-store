/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.entitlement.spec.model.Entitlement;

/**
 * EntitlementAuthorizeCallbackFactory.
 */
public class EntitlementAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Entitlement> {
    @Override
    public AuthorizeCallback<Entitlement> create(Entitlement entity) {
        return new EntitlementAuthorizeCallback(this, entity);
    }

    public AuthorizeCallback<Entitlement> create(Long userId) {
        Entitlement entity = new Entitlement();
        entity.setUserId(userId);
        return create(entity);
    }
}
