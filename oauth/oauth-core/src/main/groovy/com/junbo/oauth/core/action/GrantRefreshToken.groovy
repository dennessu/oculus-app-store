/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.RefreshToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class GrantRefreshToken implements Action {
    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def accessToken = ServiceContextUtil.getAccessToken(context)
        def appClient = ServiceContextUtil.getAppClient(context)

        Assert.notNull(accessToken)
        Assert.notNull(appClient)

        if (accessToken.scopes.contains(OAuthInfoUtil.OFFLINE_SCOPE)) {
            String salt = parameterMap.getFirst(OAuthParameters.SALT)
            RefreshToken refreshToken = tokenGenerationService.generateRefreshToken(appClient, accessToken, salt)

            ServiceContextUtil.setRefreshToken(context, refreshToken)
        }

        return true
    }
}
