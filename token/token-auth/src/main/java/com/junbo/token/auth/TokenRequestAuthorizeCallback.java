/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.token.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.common.id.UserId;
import com.junbo.token.spec.model.*;

/**
 * com.junbo.token.auth.TokenRequestAuthorizeCallback.
 */
public class TokenRequestAuthorizeCallback extends AbstractAuthorizeCallback<TokenRequest> {
    TokenRequestAuthorizeCallback(TokenRequestAuthorizeCallbackFactory factory, TokenRequest entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "token-request";
    }

    @Override
    protected Object getEntityIdByPropertyPath(String propertyPath) {
        if ("organization".equals(propertyPath)) {
            return getEntity().getOrganizationId();
        }

        return super.getEntityIdByPropertyPath(propertyPath);
    }
}
