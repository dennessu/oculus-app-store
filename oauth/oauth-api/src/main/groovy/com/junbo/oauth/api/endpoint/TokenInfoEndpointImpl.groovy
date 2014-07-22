/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
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
class TokenInfoEndpointImpl implements TokenInfoEndpoint {
    /**
     * The OAuthTokenService to handle the token related logic.
     */
    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
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
            throw AppCommonErrors.INSTANCE.parameterRequired('access_token').exception()
        }

        // Retrieve the access token with the tokenValue.
        AccessToken accessToken = tokenService.getAccessToken(tokenValue)

        // Throw exception when the tokenValue is invalid or the access token has been expired.
        if (accessToken == null) {
            throw AppErrors.INSTANCE.invalidAccessToken(tokenValue).exception()
        }

        if (accessToken.isExpired()) {
            throw AppErrors.INSTANCE.expiredAccessToken(tokenValue).exception()
        }

        // Return the token information.
        TokenInfo tokenInfo = new TokenInfo(
                clientId: accessToken.clientId,
                sub: new UserId(accessToken.userId),
                scopes: StringUtils.collectionToDelimitedString(accessToken.scopes, ' '),
                expiresIn: (Long) (accessToken.expiredBy.time - System.currentTimeMillis()) / 1000,
                ipAddress: accessToken.ipAddress
        )

        return Promise.pure(tokenInfo)
    }
}
