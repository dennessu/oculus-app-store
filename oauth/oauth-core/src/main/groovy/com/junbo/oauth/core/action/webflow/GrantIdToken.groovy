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
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.spec.model.IdToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * GrantIdToken.
 */
@CompileStatic
class GrantIdToken implements Action {

    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def oauthInfo = contextWrapper.oauthInfo
        def appClient = contextWrapper.appClient
        def authorizationCode = contextWrapper.authorizationCode
        def accessToken = contextWrapper.accessToken
        def loginState = contextWrapper.loginState

        if (!OAuthInfoUtil.isIdTokenNeeded(oauthInfo)) {
            return Promise.pure(null)
        }

        String nonce = parameterMap.getFirst(OAuthParameters.NONCE)

        if (!StringUtils.hasText(nonce)) {
            nonce = oauthInfo.nonce
        }

        if (!StringUtils.hasText(nonce)) {
            throw AppExceptions.INSTANCE.missingNonce().exception()
        }

        Date lastAuthDate = null

        String maxAge = parameterMap.getFirst(OAuthParameters.MAX_AGE)

        if (StringUtils.hasText(maxAge)) {
            lastAuthDate = loginState.lastAuthDate
        }

        IdToken idToken = tokenGenerationService.generateIdToken(appClient, appClient.idTokenIssuer,
                loginState.userId, nonce, lastAuthDate, authorizationCode, accessToken)

        contextWrapper.idToken = idToken

        return Promise.pure(null)
    }
}
