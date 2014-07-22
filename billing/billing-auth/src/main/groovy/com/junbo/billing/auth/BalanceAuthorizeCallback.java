/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.UserId;

/**
 * BalanceAuthorizeCallback.
 */
public class BalanceAuthorizeCallback extends AbstractAuthorizeCallback<Balance> {
    protected BalanceAuthorizeCallback(BalanceAuthorizeCallbackFactory factory, Balance entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "balances";
    }

    @Override
    protected UserId getUserOwnerId() {
        Balance balance = getEntity();
        if (balance != null) {
            return balance.getUserId();
        }

        return null;
    }
}
