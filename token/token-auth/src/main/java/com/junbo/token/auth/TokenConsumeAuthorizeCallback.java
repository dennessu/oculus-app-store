/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.token.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.common.id.UserId;
import com.junbo.token.spec.model.TokenConsumption;

/**
 * TokenItemAuthorizeCallbackFactory.
 */
public class TokenConsumeAuthorizeCallback extends AbstractAuthorizeCallback<TokenConsumption> {
    TokenConsumeAuthorizeCallback(TokenConsumeAuthorizeCallbackFactory factory, TokenConsumption entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "token-consumption";
    }

    @Override
    protected UserId getUserOwnerId() {
        TokenConsumption entity = getEntity();
        if (entity != null) {
            return new UserId(getEntity().getUserId());
        }

        return null;
    }
}
