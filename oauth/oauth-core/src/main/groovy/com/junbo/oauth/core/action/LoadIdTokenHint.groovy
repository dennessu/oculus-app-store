/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.IdToken
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * Javadoc.
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
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def appClient = ServiceContextUtil.getAppClient(context)

        Assert.notNull(appClient)

        String idTokenHint = parameterMap.getFirst(OAuthParameters.ID_TOKEN_HINT)

        if (!StringUtils.hasText(idTokenHint)) {
            return true
        }

        IdToken idToken = tokenGenerationService.parseIdToken(appClient, idTokenHint)

        String issuer = GrantIdToken.getIssuer(context)

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

        ServiceContextUtil.setLoginState(context, loginState)

        return true
    }
}
