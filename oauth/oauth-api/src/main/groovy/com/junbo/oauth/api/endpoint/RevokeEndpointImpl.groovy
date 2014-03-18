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
import com.junbo.oauth.spec.endpoint.RevokeEndpoint
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response

/**
 * RevokeEndpointImpl.
 */
@CompileStatic
class RevokeEndpointImpl implements RevokeEndpoint {
    private TokenService tokenService
    private ClientRepository clientRepository

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
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
}
