/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class GenerateAccessTokenResponse implements Action {
    @Override
    boolean execute(ServiceContext context) {
        def accessToken = ServiceContextUtil.getAccessToken(context)
        def refreshToken = ServiceContextUtil.getRefreshToken(context)
        def idToken = ServiceContextUtil.getIdToken(context)

        Assert.notNull(accessToken)

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
                accessToken: accessToken.tokenValue,
                tokenType: TokenType.BEARER.paramName,
                expiresIn: (Long) (accessToken.expiredBy.time - System.currentTimeMillis()) / 1000
        )

        if (refreshToken != null) {
            accessTokenResponse.refreshToken = refreshToken.tokenValue
        }

        if (idToken != null) {
            accessTokenResponse.idToken = idToken.tokenValue
        }

        ServiceContextUtil.setAccessTokenResponse(context, accessTokenResponse)

        return true
    }
}
