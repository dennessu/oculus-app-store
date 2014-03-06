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
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.model.RefreshToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * GrantTokenByRefreshToken.
 */
@CompileStatic
class GrantTokenByRefreshToken implements Action {

    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def appClient = contextWrapper.appClient
        def oauthInfo = contextWrapper.oauthInfo

        String token = parameterMap.getFirst(OAuthParameters.REFRESH_TOKEN)

        if (!StringUtils.hasText(token)) {
            throw AppExceptions.INSTANCE.missingRefreshToken().exception()
        }

        RefreshToken refreshToken = tokenGenerationService.getAndRemoveRefreshToken(token)
        if (refreshToken == null) {
            throw AppExceptions.INSTANCE.invalidRefreshToken(token).exception()
        }

        if (refreshToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredRefreshToken(token).exception()
        }

        AccessToken accessToken = refreshToken.accessToken
        Assert.notNull(accessToken)

        if (refreshToken.clientId != appClient.clientId) {
            throw AppExceptions.INSTANCE.differentClientId(refreshToken.clientId, appClient.clientId).exception()
        }

        Set<String> scopesParam = oauthInfo.scopes

        String[] extraScopes = scopesParam.findAll {
            String scope -> !accessToken.scopes.contains(scope)
        }

        if (extraScopes.length > 0) {
            throw AppExceptions.INSTANCE.outboundScope().exception()
        }

        LoginState loginState = new LoginState(
                userId: refreshToken.userId,
                lastAuthDate: new Date()
        )
        contextWrapper.loginState = loginState

        def newAccessToken = tokenGenerationService.generateAccessToken(appClient, refreshToken.userId, scopesParam)
        contextWrapper.accessToken = newAccessToken

        def newRefreshToken = tokenGenerationService.generateRefreshToken(appClient, newAccessToken, refreshToken)
        contextWrapper.refreshToken = newRefreshToken

        return Promise.pure(null)
    }
}
