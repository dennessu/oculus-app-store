/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.util.AuthorizationHeaderUtil
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.db.repo.ConsentRepository
import com.junbo.oauth.spec.endpoint.RevokeEndpoint
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.model.Consent
import com.junbo.oauth.spec.model.RefreshToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response

/**
 * RevokeEndpointImpl.
 */
@CompileStatic
class RevokeEndpointImpl implements RevokeEndpoint {
    private static final String CONSENT_MANAGE_SCOPE = 'consent.manage'
    private TokenService tokenService
    private ClientRepository clientRepository
    private ConsentRepository consentRepository

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setConsentRepository(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository
    }

    @Override
    Promise<Response> revoke(String authorization, String token, String tokenTypeHint) {
        if (StringUtils.isEmpty(authorization)) {
            throw AppExceptions.INSTANCE.missingAuthorization().exception()
        }

        if (StringUtils.isEmpty(token)) {
            throw AppExceptions.INSTANCE.missingAccessToken().exception()
        }

        def clientCredential = AuthorizationHeaderUtil.extractClientCredential(authorization)

        Client client = clientRepository.getClient(clientCredential.clientId)

        if (client == null) {
            throw AppExceptions.INSTANCE.invalidClientId(clientCredential.clientId).exception()
        }

        if (client.clientSecret != clientCredential.clientSecret) {
            throw AppExceptions.INSTANCE.invalidClientSecret(clientCredential.clientSecret).exception()
        }

        if (tokenService.isValidAccessToken(token)) {
            tokenService.revokeAccessToken(token, client)
        } else if (tokenService.isValidRefreshToken(token)) {
            tokenService.revokeRefreshToken(token, client)
        } else {
            throw AppExceptions.INSTANCE.invalidTokenType().exception()
        }

        return Promise.pure(Response.ok().build())
    }

    @Override
    Promise<Response> revokeConsent(String authorization, String clientId) {
        if (StringUtils.isEmpty(authorization)) {
            throw AppExceptions.INSTANCE.missingAuthorization().exception()
        }

        if (StringUtils.isEmpty(clientId)) {
            throw AppExceptions.INSTANCE.missingClientId().exception()
        }

        Client client = clientRepository.getClient(clientId)

        if (client == null) {
            throw AppExceptions.INSTANCE.invalidClientId(clientId).exception()
        }

        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        if (!accessToken.scopes.contains(CONSENT_MANAGE_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        Consent consent = consentRepository.getConsent(accessToken.userId, clientId)

        if (consent != null) {
            consentRepository.deleteConsent(consent)
        }

        def accessTokens = tokenService.getAccessTokenByUserIdClientId(accessToken.userId, clientId)

        accessTokens.each { AccessToken token ->
            tokenService.revokeAccessToken(token.tokenValue, client)
        }

        def refreshTokens = tokenService.getRefreshTokenByUserIdClientId(accessToken.userId, clientId)

        refreshTokens.each { RefreshToken token ->
            tokenService.revokeRefreshToken(token.tokenValue, client)
        }

        return Promise.pure(Response.ok().build())
    }
}
