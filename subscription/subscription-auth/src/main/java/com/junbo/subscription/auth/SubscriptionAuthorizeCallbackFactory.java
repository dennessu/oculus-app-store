/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.subscription.spec.model.Subscription;

/**
 * SubscriptionAuthorizeCallbackFactory.
 */
public class SubscriptionAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Subscription> {
    @Override
    public AuthorizeCallback<Subscription> create(Subscription entity) {
        return new SubscriptionAuthorizeCallback(this, entity);
    }
}
