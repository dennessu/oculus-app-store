package com.junbo.csr.rest.endpoint

import com.junbo.common.error.AppErrorException
import com.junbo.csr.spec.endpoint.CsrActionEndpoint
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrCredential
import com.junbo.csr.spec.model.CsrToken
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenRequest
import com.junbo.oauth.spec.model.AccessTokenResponse
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-8.
 */
@CompileStatic
class CsrActionEndpointImpl implements CsrActionEndpoint {
    private TokenEndpoint tokenEndpoint
    private String clientId
    private String clientSecret

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

    @Override
    Promise<CsrToken> login(CsrCredential csrCredential) {
        if (csrCredential == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        def username = csrCredential.username
        def password = csrCredential.password

        if (username == null || password == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        return this.postToken(new AccessTokenRequest(
                clientId: clientId,
                clientSecret: clientSecret,
                grantType: 'PASSWORD',
                scope: 'offline',
                username: username,
                password: password
        ))
    }

    @Override
    Promise<CsrToken> refresh(String refreshToken) {
        if (refreshToken == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        return this.postToken(new AccessTokenRequest(
                clientId: clientId,
                clientSecret: clientSecret,
                grantType: 'REFRESH_TOKEN',
                scope: 'offline',
                refreshToken: refreshToken
        ))
    }

    private Promise<CsrToken> postToken(AccessTokenRequest request) {
        return tokenEndpoint.postToken(request).recover { Throwable throwable ->
            if (throwable instanceof AppErrorException) {
                AppErrorException appError = (AppErrorException) throwable
                throw appError
            }
            else {
                throw AppErrors.INSTANCE.getAccessTokenFailed().exception()
            }
        }.then { AccessTokenResponse tokenResponse ->
            if (tokenResponse == null || tokenResponse.accessToken == null || tokenResponse.refreshToken == null || tokenResponse.expiresIn == null) {
                throw AppErrors.INSTANCE.getAccessTokenFailed().exception()
            }

            def csrToken = new CsrToken(
                    accessToken: tokenResponse.accessToken,
                    refreshToken: tokenResponse.refreshToken,
                    expiresIn: tokenResponse.expiresIn
            )

            return Promise.pure(csrToken)
        }
    }
}
