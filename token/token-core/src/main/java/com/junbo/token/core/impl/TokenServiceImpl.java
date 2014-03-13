/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.core.impl;

import com.junbo.langur.core.promise.Promise;
import com.junbo.token.core.TokenService;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.model.TokenOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * token service implementation.
 */
public class TokenServiceImpl implements TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Override
    public Promise<TokenSet> createTokenSet(TokenSet request) {
        return null;
    }

    @Override
    public Promise<TokenSet> getTokenSet(Long tokenSetId) {
        return null;
    }

    @Override
    public Promise<TokenOrder> createTokenOrder(TokenOrder request) {
        return null;
    }

    @Override
    public Promise<TokenOrder> getTokenOrder(Long tokenOrderId) {
        return null;
    }

    @Override
    public Promise<TokenItem> consumeToken(String token) {
        return null;
    }

    @Override
    public Promise<TokenItem> updateToken(TokenItem token) {
        return null;
    }

    @Override
    public Promise<TokenItem> getToken(String token) {
        return null;
    }
}
