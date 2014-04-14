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

import javax.ws.rs.*;

/**
 * token resource implementation.
 */
public class TokenResourceImpl implements TokenResource{

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenResourceImpl.class);
    @Autowired
    private TokenService tokenService;

    @Override
    public Promise<OrderRequest> postOrder(OrderRequest request) {
        CommonUtil.preValidation(request);
        return tokenService.createOrderRequest(request).
                then(new Promise.Func<OrderRequest, Promise<OrderRequest>>() {
            @Override
            public Promise<OrderRequest> apply(OrderRequest request) {
                CommonUtil.postFilter(request);
                return Promise.pure(request);
            }
        });
    }

    @Override
    public Promise<OrderRequest> getOrderById(TokenOrderId tokenOrderId) {
        return tokenService.getOrderRequest(tokenOrderId.getValue());
    }

    @Override
    public Promise<ResultList<OrderRequest>> searchOrder(@BeanParam TokenOrderSearchParam searchParam,
                                                       @BeanParam PageMetaData pageMetadata) {
        return null;
    }

    @Override
    public Promise<TokenItem> consumeToken(String tokenString, TokenConsumption consumption) {
        return tokenService.consumeToken(tokenString, consumption);
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

