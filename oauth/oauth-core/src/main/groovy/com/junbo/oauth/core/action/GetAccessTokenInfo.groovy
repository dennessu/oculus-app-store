/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
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
    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String token = parameterMap.getFirst(OAuthParameters.ACCESS_TOKEN)

        if (!StringUtils.hasText(token)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('access_token').exception()
        }

        AccessToken accessToken = tokenService.getAccessToken(token)

        if (accessToken == null) {
            throw AppErrors.INSTANCE.invalidAccessToken(token).exception()
        }

        if (accessToken.isExpired()) {
            throw AppErrors.INSTANCE.expiredAccessToken(token).exception()
        }

        TokenInfo tokenInfo = new TokenInfo(
                clientId: accessToken.clientId,
                sub: new UserId(accessToken.userId),
                scopes: StringUtils.collectionToDelimitedString(accessToken.scopes, ' '),
                expiresIn: (Long) (accessToken.expiredBy.time - System.currentTimeMillis()) / 1000
        )

        contextWrapper.tokenInfo = tokenInfo
        return Promise.pure(null)
    }
}
