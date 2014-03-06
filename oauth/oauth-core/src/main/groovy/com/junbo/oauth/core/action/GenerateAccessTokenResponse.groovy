/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import org.springframework.util.Assert

/**
 * GenerateAccessTokenResponse
 */
@CompileStatic
class GenerateAccessTokenResponse implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def accessToken = contextWrapper.accessToken
        def refreshToken = contextWrapper.refreshToken
        def idToken = contextWrapper.idToken

        Assert.notNull(accessToken, 'access token is null')

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

        contextWrapper.accessTokenResponse = accessTokenResponse

        return Promise.pure(null)
    }
}
