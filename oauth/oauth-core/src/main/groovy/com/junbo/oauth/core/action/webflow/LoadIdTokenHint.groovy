/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action.webflow

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.spec.model.IdToken
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * LoadIdTokenHint.
 */
@CompileStatic
class LoadIdTokenHint implements Action {
    private static final Long MILLISECONDS_PER_SECOND = 1000L

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

        Assert.notNull(appClient, 'appClient is null')

        String nonce = parameterMap.getFirst(OAuthParameters.NONCE)

        if (StringUtils.hasText(nonce)) {
            def oauthInfo = contextWrapper.oauthInfo
            oauthInfo.nonce = nonce
        }

        String idTokenHint = parameterMap.getFirst(OAuthParameters.ID_TOKEN_HINT)

        if (!StringUtils.hasText(idTokenHint)) {
            return Promise.pure(null)
        }

        IdToken idToken = tokenGenerationService.parseIdToken(appClient, idTokenHint)

        String issuer = appClient.idTokenIssuer

        if (issuer != idToken.iss) {
            throw AppExceptions.INSTANCE.invalidIdTokenIssuer().exception()
        }

        if (!idToken.aud.contains(appClient.clientId)) {
            throw AppExceptions.INSTANCE.invalidIdTokenAudience(appClient.clientId).exception()
        }

        if (idToken.exp < System.currentTimeMillis() / MILLISECONDS_PER_SECOND) {
            throw AppExceptions.INSTANCE.expiredIdToken().exception()
        }

        LoginState loginState = new LoginState(
                userId: Long.parseLong(idToken.sub)
        )

        if (idToken.authTime != null) {
            loginState.lastAuthDate = new Date(idToken.authTime * MILLISECONDS_PER_SECOND)
        }

        contextWrapper.loginState = loginState

        return Promise.pure(null)
    }
}
