/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.token.spec.model.*;
import com.junbo.token.spec.resource.TokenResource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;

/**
 * token resource implementation.
 */
public class TokenResourceImpl implements TokenResource{
    @Override
    public Promise<TokenOrder> postOrderGeneration(TokenOrder request) {
        return null;
    }

    @Override
    public Promise<TokenOrder> postOrderUpload(TokenOrder request) {
        return null;
    }

    @Override
    public Promise<TokenOrder> getById(Long tokenOrderId) {
        return null;
    }

    @Override
    public Promise<ResultList<TokenOrder>> searchPaymentInstrument(@BeanParam TokenOrderSearchParam searchParam,
                                                                   @BeanParam PageMetaData pageMetadata) {
        return null;
    }

    @Override
    public Promise<Response> delete(String tokenString) {
        return null;
    }

    @Override
    public Promise<TokenItem> update(String tokenString) {
        return null;
    }

    @Override
    public Promise<TokenItem> getToken(String tokenString) {
        return null;
    }
}

