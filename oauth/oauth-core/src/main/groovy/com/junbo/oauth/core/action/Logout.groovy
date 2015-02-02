/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.model.IdToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Logout.
 */
@CompileStatic
class Logout implements Action {
    private final static Logger LOGGER = LoggerFactory.getLogger(Logout)

    private LoginStateRepository loginStateRepository
    private RememberMeTokenRepository rememberMeTokenRepository
    private OAuthTokenService tokenService
    private ClientRepository clientRepository

    private String confirmationUri
    private String defaultConfirmationUri

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Required
    void setRememberMeTokenRepository(RememberMeTokenRepository rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository
    }

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setConfirmationUri(String confirmationUri) {
        this.confirmationUri = confirmationUri
    }

    @Required
    void setDefaultConfirmationUri(String defaultConfirmationUri) {
        this.defaultConfirmationUri = defaultConfirmationUri
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def cookieMap = contextWrapper.cookieMap

        IdToken idToken = null
        String idTokenHint = parameterMap.getFirst(OAuthParameters.ID_TOKEN_HINT)
        String postLogoutRedirectUri = this.defaultConfirmationUri
        if (contextWrapper.viewCountry != null) {
            postLogoutRedirectUri = postLogoutRedirectUri.replaceFirst('/:country', "/$contextWrapper.viewCountry")
        }
        if (contextWrapper.viewLocale != null) {
            postLogoutRedirectUri = postLogoutRedirectUri.replaceFirst('/:locale', "/$contextWrapper.viewLocale")
        }

        if (StringUtils.hasText(idTokenHint)) {
            Client client = null
            try {
                idToken = tokenService.parseIdToken(idTokenHint)

                client = clientRepository.getClient(idToken.aud)
                if (client == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('id_token_hint').exception()
                }

                if (new Date().time / 1000 > idToken.exp) {
                    throw AppErrors.INSTANCE.expiredIdToken().exception()
                }
            }
            catch(AppErrorException exception) {
                LOGGER.error('Error parsing the id_token', exception)
            }

            if (client != null) {
                postLogoutRedirectUri = parameterMap.getFirst(OAuthParameters.POST_LOGOUT_REDIRECT_URI)
                if (StringUtils.isEmpty(postLogoutRedirectUri)) {
                    postLogoutRedirectUri = client.defaultLogoutRedirectUri
                }
                else {
                    boolean allowed = client.logoutRedirectUris.any {
                        String allowedLogoutRedirectUri -> UriUtil.match(postLogoutRedirectUri, allowedLogoutRedirectUri)
                    }

                    if (!allowed) {
                        throw AppErrors.INSTANCE.invalidPostLogoutRedirectUri(postLogoutRedirectUri).exception()
                    }
                }
            }
        }
        contextWrapper.redirectUri = postLogoutRedirectUri

        def rememberMeCookie = cookieMap.get(OAuthParameters.COOKIE_REMEMBER_ME)
        if (rememberMeCookie != null) {
            def rememberMeToken = rememberMeTokenRepository.get(rememberMeCookie.value)
            if (rememberMeToken == null) {
                LOGGER.warn("The login state $rememberMeCookie.value is invalid, silently ignore")
            }

            contextWrapper.rememberMeToken = rememberMeToken
        }

        def loginStateCookie = cookieMap.get(OAuthParameters.COOKIE_LOGIN_STATE)
        if (loginStateCookie != null && StringUtils.hasText(loginStateCookie.value)) {
            def loginState = loginStateRepository.get(loginStateCookie.value)

            if (loginState == null) {
                LOGGER.warn("The login state $loginStateCookie.value is invalid, silently ignore")
            } else {

                contextWrapper.loginState = loginState

                if (idToken != null) {
                    Long userId = IdFormatter.decodeId(UserId, idToken.sub)
                    if (userId == loginState.userId) {
                        return Promise.pure(new ActionResult('redirectToLogoutRedirectUri'))
                    }
                }

                return Promise.pure(new ActionResult('redirectToConfirmation'))
            }
        }

        return Promise.pure(new ActionResult('redirectWithoutClearCookie'))
    }
}
