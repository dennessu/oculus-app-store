/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.token.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.token.spec.model.*;

/**
 * com.junbo.token.auth.TokenRequestAuthorizeCallbackFactory.
 */
public class TokenRequestAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<TokenRequest> {

    @Override
    public AuthorizeCallback<TokenRequest> create(TokenRequest entity) {
        return new TokenRequestAuthorizeCallback(this, entity);
    }

    public AuthorizeCallback<TokenRequest> create(Long organizationId) {
        TokenRequest entity = new TokenRequest();
        entity.setOrganizationId(organizationId);
        return create(entity);
    }
}
