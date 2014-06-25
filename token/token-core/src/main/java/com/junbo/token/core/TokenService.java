/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.core;

import com.junbo.langur.core.promise.Promise;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.model.TokenRequest;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.internal.TokenOrder;
import org.springframework.transaction.annotation.Transactional;


/**
 * token service.
 */
public interface TokenService {
    @Transactional
    Promise<TokenSet> createTokenSet(TokenSet request);
    @Transactional(readOnly = true)
    Promise<TokenSet> getTokenSet(Long tokenSetId);
    @Transactional
    Promise<TokenOrder> createTokenOrder(TokenOrder request);
    @Transactional
    Promise<TokenRequest> createOrderRequest(TokenRequest request);
    @Transactional(readOnly = true)
    Promise<TokenRequest> getOrderRequest(Long tokenOrderId);
    @Transactional(readOnly = true)
    Promise<TokenOrder> getTokenOrder(Long tokenOrderId);
    @Transactional
    Promise<TokenConsumption> consumeToken(String token, TokenConsumption consumption);
    @Transactional
    Promise<TokenItem> updateToken(String tokenString, TokenItem token);
    @Transactional(readOnly = true)
    Promise<TokenItem> getToken(String token);
}
