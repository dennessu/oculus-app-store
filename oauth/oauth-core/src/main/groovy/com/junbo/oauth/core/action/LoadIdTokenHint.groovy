/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.spec.model.IdToken
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * LoadIdTokenHint.
 */
@CompileStatic
class LoadIdTokenHint implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadIdTokenHint)

    private static final Long MILLISECONDS_PER_SECOND = 1000L

    private OAuthTokenService tokenService

    private UserService userService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def client = contextWrapper.client

        Assert.notNull(client, 'client is null')

        // todo: duplicate in ValidateNonce?
        String nonce = parameterMap.getFirst(OAuthParameters.NONCE)

        if (StringUtils.hasText(nonce)) {
            def oauthInfo = contextWrapper.oauthInfo
            oauthInfo.nonce = nonce
        }

        String idTokenHint = parameterMap.getFirst(OAuthParameters.ID_TOKEN_HINT)

        if (!StringUtils.hasText(idTokenHint)) {
            return Promise.pure(null)
        }

        IdToken idToken = tokenService.parseIdToken(idTokenHint)

        String issuer = client.idTokenIssuer

        if (issuer != idToken.iss) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id_token_hint').exception()
        }

        // todo: this isn't correct.
        if (!idToken.aud.contains(client.clientId)) {
            throw AppErrors.INSTANCE.invalidIdTokenAudience(client.clientId).exception()
        }

        if (idToken.exp < System.currentTimeMillis() / MILLISECONDS_PER_SECOND) {
            throw AppErrors.INSTANCE.expiredIdToken().exception()
        }

        LoginState loginState = new LoginState(
                userId: Long.parseLong(idToken.sub)
        )

        if (idToken.authTime != null) {
            loginState.lastAuthDate = new Date(idToken.authTime * MILLISECONDS_PER_SECOND)
        }

        contextWrapper.loginState = loginState

        try {
            contextWrapper.user = userService.getUser(new UserId(loginState.userId)).get()
        } catch (AppErrorException e) {
            if (e.error.httpStatusCode == 404) {
                LOGGER.warn("The idTokenHint is invalid, silently ignore")
                contextWrapper.loginState = null
                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }
}
