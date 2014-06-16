/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.billing.spec.model.Balance;

/**
 * BalanceAuthorizeCallbackFactory.
 */
public class BalanceAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Balance> {
    @Override
    public AuthorizeCallback<Balance> create(Balance entity) {
        return new BalanceAuthorizeCallback(this, entity);
    }
}
