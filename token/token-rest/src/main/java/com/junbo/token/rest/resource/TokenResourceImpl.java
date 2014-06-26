/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.rest.resource;

import com.junbo.common.id.TokenOrderId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.token.common.CommonUtil;
import com.junbo.token.core.TokenService;
import com.junbo.token.spec.model.*;
import com.junbo.token.spec.resource.TokenResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * token resource implementation.
 */
public class TokenResourceImpl implements TokenResource{

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenResourceImpl.class);
    @Autowired
    private TokenService tokenService;

    @Override
    public Promise<TokenRequest> postOrder(TokenRequest request) {
        CommonUtil.preValidation(request);
        return tokenService.createOrderRequest(request).
                then(new Promise.Func<TokenRequest, Promise<TokenRequest>>() {
            @Override
            public Promise<TokenRequest> apply(TokenRequest request) {
                CommonUtil.postFilter(request);
                return Promise.pure(request);
            }
        });
    }

    @Override
    public Promise<TokenRequest> getOrderById(TokenOrderId tokenOrderId) {
        return tokenService.getOrderRequest(tokenOrderId.getValue());
    }

    @Override
    public Promise<TokenConsumption> consumeToken(TokenConsumption consumption) {
        return tokenService.consumeToken(consumption.getTokenString(), consumption);
    }

    @Override
    public Promise<TokenItem> updateToken(String tokenString, TokenItem token) {
        return tokenService.updateToken(tokenString, token);
    }

    @Override
    public Promise<TokenItem> getToken(String tokenString) {
        return tokenService.getToken(tokenString);
    }
}

