package com.junbo.authorization

import com.junbo.langur.core.client.AccessTokenProvider
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenRequest
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 5/17/2014.
 */
class ClientCredentialsAccessTokenProvider implements AccessTokenProvider {

    private TokenEndpoint tokenEndpoint

    private String clientId

    private String clientSecret

    private String scope

    private Long deflectionInSeconds

    private volatile TokenCache tokenCache

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
        if (tokenCache != null && new Date().before(tokenCache.expiresBy)) {
            return tokenCache.tokenValue
        } else {
            def tokenResponse = tokenEndpoint.postToken(new AccessTokenRequest(
                    grantType: 'client_credentials',
                    clientId: clientId,
                    clientSecret: clientSecret,
                    scope: scope
            )).get();

            def newTokenCache = new TokenCache(tokenResponse.accessToken, tokenResponse.expiresIn)
            tokenCache = newTokenCache

            return newTokenCache.tokenValue
        }
    }

    private class TokenCache {
        private final String tokenValue
        private final Date expiresBy

        TokenCache(String tokenValue, Long expiresIn) {
            this.tokenValue = tokenValue
            this.expiresBy = new Date(new Date().getTime() + expiresIn * 1000 - deflectionInSeconds * 1000)
        }

        String getTokenValue() {
            return tokenValue
        }

        Date getExpiresBy() {
            return expiresBy
        }
    }
}
