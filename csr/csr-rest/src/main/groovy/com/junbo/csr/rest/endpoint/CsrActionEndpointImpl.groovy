package com.junbo.csr.rest.endpoint

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.spec.endpoint.CsrActionEndpoint
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrCredential
import com.junbo.csr.spec.model.CsrToken
import com.junbo.csr.spec.model.SearchForm
import com.junbo.csr.spec.def.SearchType
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenRequest
import com.junbo.oauth.spec.model.AccessTokenResponse
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-8.
 */
@CompileStatic
class CsrActionEndpointImpl implements CsrActionEndpoint {
    private IdentityService identityService
    private TokenEndpoint tokenEndpoint
    private String clientId
    private String clientSecret
    private String tokenScope
    private final Logger LOGGER = LoggerFactory.getLogger(CsrActionEndpointImpl)

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
    void setIdentityService(IdentityService identityService) {
        this.identityService = identityService
    }

    @Required
    void setTokenScope(String tokenScope) {
        this.tokenScope = tokenScope
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
                scope: 'offline csr',
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
                scope: 'offline csr',
                refreshToken: refreshToken
        ))
    }

    @Override
    Promise<Results<User>> searchUsers(SearchForm searchForm) {
        if (searchForm == null || searchForm.type == null || searchForm.value == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        def results = new Results<User>(items: [])
        User user = null
        switch (searchForm.type) {
            case SearchType.USERNAME:
                user = identityService.getUserByUsername(searchForm.value).get()
                break
            case SearchType.USERID:
                Long userId = IdFormatter.decodeId(UserId, searchForm.value)
                user = identityService.getUserById(new UserId(userId)).get()
                break
            case SearchType.FULLNAME:
                return identityService.getUserByUserFullName(searchForm.value)
            case SearchType.EMAIL:
                return identityService.getUserByUserEmail(searchForm.value)
            case SearchType.PHONENUMBER:
                return identityService.getUserByPhoneNumber(searchForm.value)
        }

        if (user != null) {
            results.items.add(user)
        }
        return Promise.pure(results)
    }

    private Promise<CsrToken> postToken(AccessTokenRequest request) {
        return tokenEndpoint.postToken(request).recover { Throwable throwable ->
            if (throwable instanceof AppErrorException) {
                AppErrorException appError = (AppErrorException) throwable
                throw appError
            }
            else {
                LOGGER.error('post token failed', throwable)
                throw AppErrors.INSTANCE.getAccessTokenFailed().exception()
            }
        }.then { AccessTokenResponse tokenResponse ->
            if (tokenResponse == null || tokenResponse.accessToken == null || tokenResponse.refreshToken == null || tokenResponse.expiresIn == null) {
                LOGGER.error('post token returns empty access token')
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
