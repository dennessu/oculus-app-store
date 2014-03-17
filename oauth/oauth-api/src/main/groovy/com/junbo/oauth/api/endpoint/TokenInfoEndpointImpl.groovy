/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriInfo

/**
 * Javadoc.
 */
@Component
@CompileStatic
@Scope('prototype')
class TokenInfoEndpointImpl implements TokenInfoEndpoint {
    private TokenService tokenService

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<TokenInfo> getTokenInfo(UriInfo uriInfo) {
        String token = uriInfo.queryParameters.getFirst(OAuthParameters.ACCESS_TOKEN)

        if (!StringUtils.hasText(token)) {
            throw AppExceptions.INSTANCE.missingAccessToken().exception()
        }

        AccessToken accessToken = tokenService.getAccessToken(token)

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

        return Promise.pure(tokenInfo)
    }
}
