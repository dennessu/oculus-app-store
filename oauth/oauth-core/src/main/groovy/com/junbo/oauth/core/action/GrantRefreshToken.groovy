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
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.spec.model.RefreshToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * GrantRefreshToken.
 */
@CompileStatic
class GrantRefreshToken implements Action {

    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def accessToken = contextWrapper.accessToken
        def client = contextWrapper.client

        Assert.notNull(accessToken, 'accessToken is null')
        Assert.notNull(client, 'client is null')

        if (accessToken.scopes.contains(OAuthInfoUtil.OFFLINE_SCOPE)) {
            String salt = parameterMap.getFirst(OAuthParameters.SALT)
            RefreshToken refreshToken = tokenService.generateRefreshToken(client, accessToken, salt)

            accessToken.refreshTokenValue = refreshToken.tokenValue
            tokenService.updateAccessToken(accessToken)

            contextWrapper.refreshToken = refreshToken
        }

        return Promise.pure(null)
    }
}
