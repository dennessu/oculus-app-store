/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.token.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.model.TokenRequest;

/**
 * TokenItemAuthorizeCallbackFactory.
 */
public class TokenConsumeAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<TokenConsumption> {
    @Override
    public AuthorizeCallback<TokenConsumption> create(TokenConsumption entity) {
        return new TokenConsumeAuthorizeCallback(this, entity);
    }

    public AuthorizeCallback<TokenConsumption> create(Long userId) {
        TokenConsumption entity = new TokenConsumption();
        entity.setUserId(userId);
        return create(entity);
    }
}
