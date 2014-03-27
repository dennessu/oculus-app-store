/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.token.core.TokenService;
import com.junbo.token.core.exception.AppClientExceptions;
import com.junbo.token.spec.model.*;
import com.junbo.token.spec.resource.TokenResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;

/**
 * token resource implementation.
 */
public class TokenResourceImpl implements TokenResource{

    @Autowired
    private TokenService tokenService;

    @Override
    public Promise<OrderRequest> postOrder(OrderRequest request) {
        return tokenService.createOrderRequest(request);
    }

    @Override
    public Promise<OrderRequest> getOrderById(Long tokenOrderId) {
        return tokenService.getOrderRequest(tokenOrderId);
    }

    @Override
    public Promise<ResultList<OrderRequest>> searchOrder(@BeanParam TokenOrderSearchParam searchParam,
                                                       @BeanParam PageMetaData pageMetadata) {
        return null;
    }

    @Override
    public Promise<TokenItem> consumeToken(String tokenString) {
        return tokenService.consumeToken(tokenString);
    }

    @Override
    public Promise<TokenItem> updateToken(String tokenString, TokenItem token) {
        //TODO: compare hash later
        if(tokenString.equalsIgnoreCase(token.getHashValue().toString())){
            throw AppClientExceptions.INSTANCE.invalidToken(tokenString).exception();
        }
        return tokenService.updateToken(token);
    }

    @Override
    public Promise<TokenItem> getToken(String tokenString) {
        return tokenService.getToken(tokenString);
    }
}

