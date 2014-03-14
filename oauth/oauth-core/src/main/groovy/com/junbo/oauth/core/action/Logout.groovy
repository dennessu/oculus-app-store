/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
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
import org.springframework.web.util.UriComponentsBuilder

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

    private String confirmationUri

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

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setConfirmationUri(String confirmationUri) {
        this.confirmationUri = confirmationUri
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def cookieMap = contextWrapper.cookieMap

        IdToken idToken = null
        String idTokenHint = parameterMap.getFirst(OAuthParameters.ID_TOKEN_HINT)
        if (StringUtils.hasText(idTokenHint)) {
            idToken = tokenGenerationService.parseIdToken(idTokenHint)

            def client = clientRepository.getClient(idToken.aud)
            if (client == null) {
                throw AppExceptions.INSTANCE.invalidIdToken().exception()
            }

            if (new Date().time / 1000 > idToken.exp) {
                throw AppExceptions.INSTANCE.expiredIdToken().exception()
            }

            String postLogoutRedirectUri = parameterMap.getFirst(OAuthParameters.POST_LOGOUT_REDIRECT_URI)
            if (StringUtils.isEmpty(postLogoutRedirectUri)) {
                postLogoutRedirectUri = client.defaultLogoutRedirectUri
            } else {
                if (!client.logoutRedirectUris.contains(postLogoutRedirectUri)) {
                    throw AppExceptions.INSTANCE.invalidPostLogoutRedirectUri(postLogoutRedirectUri).exception()
                }
            }

            contextWrapper.redirectUriBuilder = UriComponentsBuilder.fromUriString(postLogoutRedirectUri)
        }

        def rememberMeCookie = cookieMap.get(OAuthParameters.REMEMBER_ME)
        if (rememberMeCookie != null) {
            def rememberMeToken = rememberMeTokenRepository.get(rememberMeCookie.value)
            if (rememberMeToken == null) {
                LOGGER.warn("The login state $rememberMeCookie.value is invalid, silently ignore")
            }

            contextWrapper.rememberMeToken = rememberMeToken
        }

        def loginStateCookie = cookieMap.get(OAuthParameters.LOGIN_STATE)
        if (loginStateCookie != null) {
            def loginState = loginStateRepository.get(loginStateCookie.value)

            if (loginState == null) {
                LOGGER.warn("The login state $loginStateCookie.value is invalid, silently ignore")
            } else {

                contextWrapper.loginState = loginState

                if (idToken != null) {
                    Long userId = IdFormatter.decodeId(UserId, idToken.sub)
                    if (userId != loginState.userId) {
                        def redirectUriBuilder = UriComponentsBuilder.fromUriString(confirmationUri)
                        redirectUriBuilder.queryParam(OAuthParameters.CONVERSATION_ID, contextWrapper.conversationId)
                        redirectUriBuilder.queryParam(OAuthParameters.EVENT, 'logoutConfirmation')
                        redirectUriBuilder.queryParam(OAuthParameters.USER_ID,
                                IdFormatter.encodeId(new UserId(loginState.userId)))
                        redirectUriBuilder.queryParam(OAuthParameters.ID_TOKEN_USER_ID, idToken.sub)

                        contextWrapper.redirectUriBuilder = redirectUriBuilder

                        return Promise.pure(new ActionResult('redirectToConfirmation'))
                    }

                    return Promise.pure(new ActionResult('redirectToLogoutRedirectUri'))
                }
            }
        }

        return Promise.pure(new ActionResult('clearCookie'))
    }
}
