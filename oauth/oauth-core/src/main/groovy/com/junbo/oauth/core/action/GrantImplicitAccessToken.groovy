/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.ResponseType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class GrantImplicitAccessToken implements Action {
    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    boolean execute(ServiceContext context) {
        def appClient = ServiceContextUtil.getAppClient(context)
        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)

        if (oauthInfo.responseTypes.contains(ResponseType.TOKEN)) {
            def loginState = ServiceContextUtil.getLoginState(context)
            Assert.notNull(loginState)

            AccessToken accessToken = tokenGenerationService.generateAccessToken(appClient,
                    loginState.userId, oauthInfo.scopes)
            ServiceContextUtil.setAccessToken(context, accessToken)
        }

        return true
    }
}
