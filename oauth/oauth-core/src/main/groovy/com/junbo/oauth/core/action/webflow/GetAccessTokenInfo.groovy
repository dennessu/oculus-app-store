/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action.webflow

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * GetAccessTokenInfo
 */
@CompileStatic
class GetAccessTokenInfo implements Action {
    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

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

        contextWrapper.tokenInfo = tokenInfo
        return Promise.pure(null)
    }
}
