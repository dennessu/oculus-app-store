/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.langur.core.promise.Promise;
import com.junbo.token.auth.TokenConsumeAuthorizeCallbackFactory;
import com.junbo.token.auth.TokenRequestAuthorizeCallbackFactory;
import com.junbo.token.common.CommonUtil;
import com.junbo.token.common.exception.AppErrors;
import com.junbo.token.core.TokenService;
import com.junbo.token.spec.model.*;
import com.junbo.token.spec.resource.TokenResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * token resource implementation.
 */
public class TokenResourceImpl implements TokenResource{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenResourceImpl.class);
    private TokenService tokenService;
    private AuthorizeService authorizeService;
    private TokenRequestAuthorizeCallbackFactory requestAuthCallbackFactory;
    private TokenConsumeAuthorizeCallbackFactory consumeAuthCallbackFactory;

    @Override
    public Promise<TokenRequest> postOrder(final TokenRequest request) {
        CommonUtil.preValidation(request);
        AuthorizeCallback callback = requestAuthCallbackFactory.create(request);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<TokenRequest>>() {
            @Override
            public Promise<TokenRequest> apply() {
                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }
                return tokenService.createOrderRequest(request).
                        then(new Promise.Func<TokenRequest, Promise<TokenRequest>>() {
                            @Override
                            public Promise<TokenRequest> apply(TokenRequest request) {
                                CommonUtil.postFilter(request);
                                return Promise.pure(request);
                            }
                        });
                }
            });
        }

    @Override
    public Promise<TokenRequest> getOrderById(final String tokenOrderId) {
        return tokenService.getOrderRequest(tokenOrderId)
                .then(new Promise.Func<TokenRequest, Promise<TokenRequest>>() {
                    @Override
                    public Promise<TokenRequest> apply(final TokenRequest tokenRequest) {
                        CommonUtil.postFilter(tokenRequest);
                        AuthorizeCallback callback = requestAuthCallbackFactory.create(tokenRequest);
                        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<TokenRequest>>() {
                            @Override
                            public Promise<TokenRequest> apply() {
                                if (!AuthorizeContext.hasRights("read")) {
                                    throw AppErrors.INSTANCE.tokenOrderNotFound(tokenOrderId).exception();
                                }
                                return Promise.pure(tokenRequest);
                            }
                        });
                    }
                });
    }

    @Override
    public Promise<TokenConsumption> consumeToken(final TokenConsumption consumption) {
        CommonUtil.preValidation(consumption);
        AuthorizeCallback callback = consumeAuthCallbackFactory.create(consumption);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<TokenConsumption>>() {
            @Override
            public Promise<TokenConsumption> apply() {
                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }
                return tokenService.consumeToken(consumption.getTokenString(), consumption)
                        .then(new Promise.Func<TokenConsumption, Promise<TokenConsumption>>() {
                            @Override
                            public Promise<TokenConsumption> apply(TokenConsumption consumption) {
                                CommonUtil.postFilter(consumption);
                                return Promise.pure(consumption);
                    }
                        });
            }
        });
    }

    @Override
    public Promise<TokenItem> updateToken(final String tokenString, final TokenItem token) {
        return tokenService.updateToken(tokenString, token);
    }

    @Override
    public Promise<TokenItem> getToken(String tokenString) {
        return tokenService.getToken(tokenString).then(new Promise.Func<TokenItem, Promise<TokenItem>>() {
            @Override
            public Promise<TokenItem> apply(final TokenItem tokenItem) {
                CommonUtil.postFilter(tokenItem);
                return Promise.pure(tokenItem);
            }
        });
    }

    public TokenService getTokenService() {
        return tokenService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    public void setRequestAuthCallbackFactory(TokenRequestAuthorizeCallbackFactory requestAuthCallbackFactory) {
        this.requestAuthCallbackFactory = requestAuthCallbackFactory;
    }

    public void setConsumeAuthCallbackFactory(TokenConsumeAuthorizeCallbackFactory consumeAuthCallbackFactory) {
        this.consumeAuthCallbackFactory = consumeAuthCallbackFactory;
    }
}

