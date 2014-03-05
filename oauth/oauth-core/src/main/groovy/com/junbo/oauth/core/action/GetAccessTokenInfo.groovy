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
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class GetAccessTokenInfo implements Action {
    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        String token = parameterMap.getFirst(OAuthParameters.ACCESS_TOKEN)

        if (!StringUtils.hasText(token)) {
            throw AppExceptions.INSTANCE.missingAccessToken().exception()
        }

        AccessToken accessToken = tokenGenerationService.getAccessToken(token)

        if (accessToken == null) {
            throw AppExceptions.INSTANCE.invalidAccessToken().exception()
        }

        if (accessToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredAccessToken().exception()
        }

        TokenInfo tokenInfo = new TokenInfo(
                clientId: accessToken.clientId,
                sub: accessToken.userId.toString(),
                scopes: StringUtils.collectionToDelimitedString(accessToken.scopes, ' '),
                expireIn: (Long) (accessToken.expiredBy.time - System.currentTimeMillis()) / 1000
        )

        ServiceContextUtil.setTokenInfo(context, tokenInfo)

        return true
    }
}
