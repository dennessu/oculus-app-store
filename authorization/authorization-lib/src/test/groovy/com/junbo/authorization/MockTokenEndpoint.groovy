/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic

import javax.ws.rs.core.MultivaluedMap

/**
 * MockTokenEndpoint.
 */
@CompileStatic
class MockTokenEndpoint implements TokenEndpoint {
    @Override
    Promise<AccessTokenResponse> postToken(MultivaluedMap<String, String> formParams) {
        return null
    }

    @Override
    Promise<AccessTokenResponse> postToken(String clientId, String clientSecret, String grantType, String code,
                                           String scope, String redirectUri, String username, String password,
                                           String refreshToken, String nonce) {
        return Promise.pure(new AccessTokenResponse(accessToken: '123', tokenType: TokenType.BEARER.name()))
    }
}
