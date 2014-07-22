/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UniversalId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
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
 * Default {@link com.junbo.oauth.spec.endpoint.RevokeEndpoint} implementation.
 * It contains two method, revoke token and revoke consent.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.RevokeEndpoint
 */
@CompileStatic
class RevokeEndpointImpl implements RevokeEndpoint {
    /**
     * The static field for the scope 'consent.manage'.
     */
    private static final String CONSENT_MANAGE_SCOPE = 'consent.manage'

    /**
     * The OAuthTokenService to handle token related operations.
     */
    private OAuthTokenService tokenService

    /**
     * The ClientRepository to handle client related operations.
     */
    private ClientRepository clientRepository

    /**
     * The ConsentRepository to handle consent related operations.
     */
    private ConsentRepository consentRepository

    @Required
    void setTokenService(OAuthTokenService tokenService) {
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

    /**
     * Endpoint to revoke an access token or a refresh token
     * @param authorization The http header Authorization that contains the client id and client secret in Basic format.
     * @param token The access token or refresh token to be revoked
     * @param tokenTypeHint The token type hint to be revoked, should be either access_token or refresh_token.
     * @return The raw javax.ws.rs Response.
     */
    @Override
    Promise<Response> revoke(String authorization, String token, String tokenTypeHint, String userHref) {
        // Validate the authorization, the authorization can't be empty.
        if (StringUtils.isEmpty(authorization)) {
            throw AppErrors.INSTANCE.missingAuthorization().exception()
        }

        // Validate the token, the token can't be empty.
        if (StringUtils.isEmpty(token) && StringUtils.isEmpty(userHref)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('access_token or user').exception()
        }

        // Parse the client id and client secret from the authorization.
        def clientCredential = AuthorizationHeaderUtil.extractClientCredential(authorization)

        Client client = clientRepository.getClient(clientCredential.clientId)

        // Validate the client id and client secret in the authorization.
        if (client == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('client_id', clientCredential.clientId).exception()
        }

        if (client.clientSecret != clientCredential.clientSecret) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('client_secret', clientCredential.clientSecret).exception()
        }

        if (!StringUtils.isEmpty(token)) {
            // If the token is an access token, revoke it as an access token.
            if (tokenService.isValidAccessToken(token)) {
                tokenService.revokeAccessToken(token, client)
                // If the token is a refresh token, revoke it as an refresh token.
            } else if (tokenService.isValidRefreshToken(token)) {
                tokenService.revokeRefreshToken(token, client)
            } else {
                throw AppErrors.INSTANCE.invalidTokenType().exception()
            }
        }

        if (!StringUtils.isEmpty(userHref)) {
            UniversalId userId = IdUtil.fromLink(new Link(href: userHref))
            if (userId == null || !userId instanceof UserId) {
                throw AppCommonErrors.INSTANCE.invalidId('userId', userHref).exception()
            }

            tokenService.revokeAccessTokenByUserId((userId as UserId).value, client)
        }

        // Simply return an OK response.
        return Promise.pure(Response.ok().build())
    }

    /**
     * Endpoint to revoke the consent of the user for a specific client.
     * @param authorization The http header that contains the access token in Bearer format.
     * @param clientId The client id of the consent to be revoked.
     * @return The raw javax.ws.rs Response.
     */
    @Override
    Promise<Response> revokeConsent(String authorization, String clientId) {
        // Validate the authorization, the authorization can't be empty.
        if (StringUtils.isEmpty(authorization)) {
            throw AppErrors.INSTANCE.missingAuthorization().exception()
        }

        // Validate the clientId, the clientId can't be empty.
        if (StringUtils.isEmpty(clientId)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('client_id').exception()
        }

        // Retrieve the client information.
        Client client = clientRepository.getClient(clientId)

        if (client == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('client_id', clientId).exception()
        }

        // Parse the access token from the authorization header.
        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        // The access token must have the consent.manage scope.
        if (!accessToken.scopes.contains(CONSENT_MANAGE_SCOPE)) {
            throw AppErrors.INSTANCE.insufficientScope().exception()
        }

        // Retrieve the consent of the given user and client id.
        Consent consent = consentRepository.getConsent(accessToken.userId, clientId)

        // Remove the consent in the database.
        if (consent != null) {
            consentRepository.deleteConsent(consent)
        }

        // Revoke all the access tokens and refresh tokens of this user and client id.
        def accessTokens = tokenService.getAccessTokenByUserIdClientId(accessToken.userId, clientId)

        accessTokens.each { AccessToken token ->
            tokenService.revokeAccessToken(token.tokenValue, client)
        }

        def refreshTokens = tokenService.getRefreshTokenByUserIdClientId(accessToken.userId, clientId)

        refreshTokens.each { RefreshToken token ->
            tokenService.revokeRefreshToken(token.tokenValue, client)
        }

        // Simply return an OK response.
        return Promise.pure(Response.ok().build())
    }
}
