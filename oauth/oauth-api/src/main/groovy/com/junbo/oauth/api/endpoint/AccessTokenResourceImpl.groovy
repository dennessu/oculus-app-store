/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppErrors
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
class AccessTokenResourceImpl implements AccessTokenResource {
    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    Promise<AccessToken> get(String accessToken) {
        AccessToken token = tokenService.getAccessToken(accessToken)

        if (token == null) {
            throw AppErrors.INSTANCE.invalidAccessToken(accessToken).exception()
        }

        return Promise.pure(token)
    }
}
