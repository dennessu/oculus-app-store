/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormat
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.core.util.CookieUtil
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.model.IdToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response

/**
 * Logout.
 */
@CompileStatic
class Logout implements Action {
    private final static Logger LOGGER = LoggerFactory.getLogger(Logout)

    private LoginStateRepository loginStateRepository
    private RememberMeTokenRepository rememberMeTokenRepository
    private TokenGenerationService tokenGenerationService
    private ClientRepository clientRepository

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Required
    void setRememberMeTokenRepository(RememberMeTokenRepository rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository
    }

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def cookieMap = contextWrapper.cookieMap

        IdToken idToken = null
        String idTokenHint = parameterMap.getFirst(OAuthParameters.ID_TOKEN_HINT)
        if (StringUtils.hasText(idTokenHint)) {
            idToken = tokenGenerationService.parseIdToken(idTokenHint, null)

            def client = clientRepository.getClient(idToken.aud)
            if (client == null) {
                throw AppExceptions.INSTANCE.invalidIdToken().exception()
            }

            String postLogoutRedirectUri = parameterMap.getFirst(OAuthParameters.POST_LOGOUT_REDIRECT_URI)
            if (StringUtils.isEmpty(postLogoutRedirectUri)) {
                postLogoutRedirectUri = client.defaultLogoutRedirectUri
            } else {
                if (!client.allowedLogoutRedirectUris.contains(postLogoutRedirectUri)) {
                    throw AppExceptions.INSTANCE.invalidPostLogoutRedirectUri(postLogoutRedirectUri).exception()
                }
            }

            contextWrapper.responseBuilder = Response.status(Response.Status.FOUND)
                    .location(URI.create(postLogoutRedirectUri))
        }

        def loginStateCookie = cookieMap.get(OAuthParameters.LOGIN_STATE)
        if (loginStateCookie != null) {
            def loginState = loginStateRepository.get(loginStateCookie.value)

            if (loginState == null) {
                LOGGER.warn("The login state $loginStateCookie.value is invalid, silently ignore")
            } else {
                if (idToken != null) {
                    Long userId = IdFormat.decodeFormattedId(UserId, idToken.sub)
                    if (userId != loginState.userId) {
                        throw AppExceptions.INSTANCE.invalidIdToken().exception()
                    }
                }

                loginStateRepository.delete(loginStateCookie.value)
            }

            CookieUtil.clearCookie(OAuthParameters.LOGIN_STATE, context)
        }

        def rememberMeCookie = cookieMap.get(OAuthParameters.REMEMBER_ME)
        if (rememberMeCookie != null) {
            def rememberMeToken = rememberMeTokenRepository.get(rememberMeCookie.value)
            if (rememberMeToken == null) {
                LOGGER.warn("The login state $loginStateCookie.value is invalid, silently ignore")
            }
            CookieUtil.clearCookie(OAuthParameters.REMEMBER_ME, context)
        }

        if (contextWrapper.responseBuilder == null) {
            contextWrapper.responseBuilder = Response.ok()
        }

        return Promise.pure(null)
    }
}
