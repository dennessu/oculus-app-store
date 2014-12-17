/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.provider

import com.junbo.langur.core.client.AccessTokenProvider
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * ClientCredentialsAccessTokenProvider.
 */
class ClientCredentialsAccessTokenProvider implements AccessTokenProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCredentialsAccessTokenProvider)

    private TokenEndpoint tokenEndpoint

    private String clientId

    private String clientSecret

    private String scope

    private Long deflectionInSeconds

    private TokenCache tokenCache

    private Lock cacheLock = new ReentrantLock()

    @Required
    void setTokenEndpoint(TokenEndpoint tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint
    }

    @Required
    void setClientId(String clientId) {
        this.clientId = clientId
    }

    @Required
    void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret
    }

    @Required
    void setScope(String scope) {
        this.scope = scope
    }

    @Required
    void setDeflectionInSeconds(Long deflectionInSeconds) {
        this.deflectionInSeconds = deflectionInSeconds
    }

    @Override
    String getTokenType() {
        return "Bearer"
    }

    @Override
    String getAccessToken() {
        // if the token cache is not available (null or expired), get the token for the first time
        if (tokenCache == null || new Date().after(tokenCache.tokenExpiresBy)) {
            // first try get the lock
            cacheLock.lock()
            try {
                // check the object for the second time, if the tokenCache is still null or expired, call the token endpoint
                // for the token
                if (tokenCache == null || new Date().after(tokenCache.tokenExpiresBy)) {
                    LOGGER.debug('token cache is null, try to get a token')
                    def newTokenCache = internalGetAccessToken()
                    tokenCache = newTokenCache

                    return newTokenCache.tokenValue
                    // else the token cache has been initialized by other thread, use the cached value
                } else {
                    LOGGER.debug('token cache is null, other thread has gotten a token')
                    return tokenCache.tokenValue
                }
            } finally {
                cacheLock.unlock()
            }
        // if the token cache has expired, refresh the cache
        } else if (new Date().after(tokenCache.cacheExpiresBy)){
            // The thread who gets the lock refreshes the cache
            if (cacheLock.tryLock()) {
                LOGGER.debug('token almost expired, try to get a new token')
                try {
                    def newTokenCache = internalGetAccessToken()
                    tokenCache = newTokenCache

                    return newTokenCache.tokenValue
                } finally {
                    cacheLock.unlock()
                }
            // other threads use the current value since there is still a few minutes before it really expires.
            } else {
                LOGGER.debug('token almost expired, other thread is trying to refresh the token, use the current token')
                return tokenCache.tokenValue
            }
        // if the token cache is available (not null) and not expired, directly return the cached value
        } else {
            LOGGER.debug('return cached token')
            return tokenCache.tokenValue
        }
    }

    private TokenCache internalGetAccessToken() {
        def tokenResponse = tokenEndpoint.postToken(new AccessTokenRequest(
                grantType: 'client_credentials',
                clientId: clientId,
                clientSecret: clientSecret,
                scope: scope
        )).get()

        return new TokenCache(tokenResponse.accessToken, tokenResponse.expiresIn)
    }

    private class TokenCache {
        private final String tokenValue
        private final Date cacheExpiresBy
        private final Date tokenExpiresBy

        TokenCache(String tokenValue, Long expiresIn) {
            this.tokenValue = tokenValue
            this.cacheExpiresBy = new Date(System.currentTimeMillis() + expiresIn * 1000 - deflectionInSeconds * 1000)
            this.tokenExpiresBy = new Date(System.currentTimeMillis() + expiresIn * 1000)
        }

        String getTokenValue() {
            return tokenValue
        }

        Date getCacheExpiresBy() {
            return cacheExpiresBy
        }

        Date getTokenExpiresBy() {
            return tokenExpiresBy
        }
    }
}
