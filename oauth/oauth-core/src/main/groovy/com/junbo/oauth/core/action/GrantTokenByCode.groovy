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
import com.junbo.oauth.db.repo.AuthorizationCodeRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.AuthorizationCode
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class GrantTokenByCode implements Action {
    private AuthorizationCodeRepository authorizationCodeRepository

    private TokenGenerationService tokenGenerationService

    @Required
    void setAuthorizationCodeRepository(AuthorizationCodeRepository authorizationCodeRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository
    }

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def appClient = ServiceContextUtil.getAppClient(context)

        String code = parameterMap.getFirst(OAuthParameters.CODE)
        String redirectUri = parameterMap.getFirst(OAuthParameters.REDIRECT_URI)

        if (!StringUtils.hasText(code)) {
            throw AppExceptions.INSTANCE.missingCode().exception()
        }

        AuthorizationCode authorizationCode = authorizationCodeRepository.getAndRemove(code)

        if (authorizationCode == null || authorizationCode.isExpired()) {
            throw AppExceptions.INSTANCE.invalidCode(code).exception()
        }

        if (!StringUtils.hasText(redirectUri)) {
            throw AppExceptions.INSTANCE.missingRedirectUri().exception()
        }

        if (!authorizationCode.redirectUri == redirectUri) {
            throw AppExceptions.INSTANCE.invalidRedirectUri(redirectUri).exception()
        }

        AccessToken accessToken = tokenGenerationService.generateAccessToken(appClient,
                authorizationCode.userId, authorizationCode.scopes)

        LoginState loginState = new LoginState(
                userId: authorizationCode.userId,
                lastAuthDate: authorizationCode.lastAuthDate
        )
        ServiceContextUtil.setLoginState(context, loginState)

        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        oauthInfo.setScopes(authorizationCode.scopes)
        oauthInfo.setNonce(authorizationCode.nonce)

        ServiceContextUtil.setAccessToken(context, accessToken)

        return true
    }
}
