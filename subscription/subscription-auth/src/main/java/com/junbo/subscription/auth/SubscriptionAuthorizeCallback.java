/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.common.id.UserId;
import com.junbo.subscription.spec.model.Subscription;

/**
 * SubscriptionAuthorizeCallback.
 */
public class SubscriptionAuthorizeCallback extends AbstractAuthorizeCallback<Subscription> {
    SubscriptionAuthorizeCallback(SubscriptionAuthorizeCallbackFactory factory, Subscription entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "subscriptions";
    }

    @Override
    protected UserId getUserOwnerId() {
        Subscription entity = getEntity();
        if (entity != null) {
            return new UserId(entity.getUserId());
        }

        return null;
    }
}
