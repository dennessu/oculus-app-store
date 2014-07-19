/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.spec.endpoint.RefreshTokenResource
import com.junbo.oauth.spec.model.RefreshToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * RefreshTokenResourceImpl.
 */
@CompileStatic
class RefreshTokenResourceImpl implements RefreshTokenResource {
    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    Promise<RefreshToken> get(String refreshToken) {
        RefreshToken token = tokenService.getRefreshToken(refreshToken)

        if (token == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('refresh_token', refreshToken).exception()
        }

        return Promise.pure(token)
    }
}
