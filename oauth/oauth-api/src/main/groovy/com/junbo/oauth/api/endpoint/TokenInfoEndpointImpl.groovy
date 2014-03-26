/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Default {@link com.junbo.oauth.spec.endpoint.TokenInfoEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
 */
@Component
@CompileStatic
@Scope('prototype')
class TokenInfoEndpointImpl implements TokenInfoEndpoint {
    /**
     * The TokenService to handle the token related logic.
     */
    private TokenService tokenService

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    /**
     * Endpoint to retrieve the token information of the given access token.
     * @param tokenValue The access token value to be retrieved.
     * @return The token information.
     */
    @Override
    Promise<TokenInfo> getTokenInfo(String tokenValue) {
        // Validate the tokenValue, the token value can't be empty.
        if (StringUtils.isEmpty(tokenValue)) {
            throw AppExceptions.INSTANCE.missingAccessToken().exception()
        }

        // Retrieve the access token with the tokenValue.
        AccessToken accessToken = tokenService.getAccessToken(tokenValue)

        // Throw exception when the tokenValue is invalid or the access token has been expired.
        if (accessToken == null) {
            throw AppExceptions.INSTANCE.invalidAccessToken().exception()
        }

        if (accessToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredAccessToken().exception()
        }

        // Return the token information.
        TokenInfo tokenInfo = new TokenInfo(
                clientId: accessToken.clientId,
                sub: new UserId(accessToken.userId),
                scopes: StringUtils.collectionToDelimitedString(accessToken.scopes, ' '),
                expiresIn: (Long) (accessToken.expiredBy.time - System.currentTimeMillis()) / 1000
        )

        return Promise.pure(tokenInfo)
    }
}
