/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.IdToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class GrantIdToken implements Action {
    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        def appClient = ServiceContextUtil.getAppClient(context)
        def authorizationCode = ServiceContextUtil.getAuthorizationCode(context)
        def accessToken = ServiceContextUtil.getAccessToken(context)
        def loginState = ServiceContextUtil.getLoginState(context)

        if (!OAuthInfoUtil.isIdTokenNeeded(oauthInfo)) {
            return true
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

        IdToken idToken = tokenGenerationService.generateIdToken(appClient, getIssuer(context),
                loginState.userId, nonce, lastAuthDate, authorizationCode, accessToken)

        ServiceContextUtil.setIdToken(context, idToken)

        return true
    }

    static String getIssuer(ServiceContext context) {
        def request = (ContainerRequest) ServiceContextUtil.getRequest(context)
        return request.baseUri.toString()
    }
}
