/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Default {@link com.junbo.oauth.spec.endpoint.TokenInfoEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
 */
@Component
@CompileStatic
class TokenInfoEndpointImpl implements TokenInfoEndpoint, InitializingBean {
    private static final DEFAULT_TOKEN = '00000000000000000000'

    private static final String DEFAULT_CLIENT = 'anonymous'
    /**
     * The OAuthTokenService to handle the token related logic.
     */
    private OAuthTokenService tokenService

    private String defaultScopes

    private TokenInfo defaultToken

    private Long defaultAccessTokenExpiration

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setDefaultScopes(String defaultScopes) {
        this.defaultScopes = defaultScopes
    }

    @Required
    void setDefaultAccessTokenExpiration(Long defaultAccessTokenExpiration) {
        this.defaultAccessTokenExpiration = defaultAccessTokenExpiration
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

        if (tokenValue == DEFAULT_TOKEN) {
            return Promise.pure(defaultToken)
        }

        String[] tokens = tokenValue.split(',')

        // Normal case: there is only one token
        if (tokens.length == 1) {
            return Promise.pure(get(tokenValue))
        }

        // Special Case: USER_TOKEN,SCOPE_OVERRIDE_TOKEN
        if (tokens.length == 2) {
            TokenInfo userToken = get(tokens[0])
            TokenInfo overrideToken = get(tokens[1])

            // override the scopes with the service token scope.
            if (overrideToken.sub.value == 0L) {
                return Promise.pure(new TokenInfo(
                        tokenValue: userToken.tokenValue,
                        sub: userToken.sub,
                        expiresIn: userToken.expiresIn,
                        scopes: overrideToken.scopes,
                        clientId: userToken.clientId,
                        ipAddress: userToken.ipAddress,
                        debugEnabled: userToken.debugEnabled
                ))
            } else {
                return Promise.pure(userToken)
            }
        }

        // Invalid case
        throw AppErrors.INSTANCE.invalidAccessToken(tokenValue).exception()
    }

    private TokenInfo get(String tokenValue) {
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
                tokenValue: tokenValue,
                clientId: accessToken.clientId,
                sub: new UserId(accessToken.userId),
                scopes: StringUtils.collectionToDelimitedString(accessToken.scopes, ' '),
                expiresIn: (Long) (accessToken.expiredBy.time - System.currentTimeMillis()) / 1000,
                ipAddress: accessToken.ipAddress
        )

        if (Boolean.TRUE.equals(accessToken.debugEnabled)) {
            tokenInfo.debugEnabled = true
        } else {
            tokenInfo.debugEnabled = null
        }

        return tokenInfo
    }

    @Override
    void afterPropertiesSet() throws Exception {
        defaultToken = new TokenInfo(
                sub: new UserId(0L),
                clientId: DEFAULT_CLIENT,
                scopes: defaultScopes,
                expiresIn: defaultAccessTokenExpiration
        )
    }
}
