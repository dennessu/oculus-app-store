/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.spec.model.IdToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * GrantIdToken.
 */
@CompileStatic
class GrantIdToken implements Action {

    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def client = contextWrapper.client
        def authorizationCode = contextWrapper.authorizationCode
        def accessToken = contextWrapper.accessToken
        def loginState = contextWrapper.loginState

        if (!OAuthInfoUtil.isIdTokenNeeded(oauthInfo)) {
            return Promise.pure(null)
        }

        String nonce = oauthInfo.nonce

        if (!StringUtils.hasText(nonce)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('nonce').exception()
        }

        Date lastAuthDate = null

        if (oauthInfo.maxAge != null) {
            lastAuthDate = loginState.lastAuthDate
        }

        IdToken idToken = tokenService.generateIdToken(client, client.idTokenIssuer,
                loginState.userId, nonce, lastAuthDate, authorizationCode, accessToken)

        contextWrapper.idToken = idToken

        return Promise.pure(null)
    }
}
