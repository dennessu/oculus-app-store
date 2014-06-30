/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.authorization.AuthorizeContext
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.spec.endpoint.AccessTokenResource
import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * AccessTokenResourceImpl.
 */
@Component
@CompileStatic
@Scope('prototype')
class AccessTokenResourceImpl implements AccessTokenResource {
    private static final String TOKEN_INFO_SCOPE = 'token.info'
    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    Promise<AccessToken> get(String accessToken) {
        if (!AuthorizeContext.hasScopes(TOKEN_INFO_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        AccessToken token = tokenService.getAccessToken(accessToken)

        if (token == null) {
            throw AppExceptions.INSTANCE.invalidAccessToken(accessToken).exception()
        }

        return Promise.pure(token)
    }
}
