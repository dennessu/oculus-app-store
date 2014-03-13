/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */


package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.ClientService
import com.junbo.oauth.db.repo.AccessTokenRepository
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ClientServiceImpl.
 */
@CompileStatic
class ClientServiceImpl implements ClientService {
    private static final String CLIENT_SCOPE
    private ClientRepository clientRepository
    private AccessTokenRepository accessTokenRepository

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository
    }

    @Override
    Client postClient(String authorization, Client client) {
        AccessToken accessToken = parseAccessToken(authorization)

        return null
    }

    @Override
    Client getClient(String authorization, String clientId) {
        return null
    }

    @Override
    Client updateClient(String authorization, String clientId, Client client) {
        return null
    }

    @Override
    Client resetSecret(String authorization, String clientId) {
        return null
    }

    private AccessToken parseAccessToken(String authorization) {
        String[] tokens = authorization.split(' ')
        if (tokens.length != 2 || tokens[0].equalsIgnoreCase(TokenType.BEARER.name())) {
            throw AppExceptions.INSTANCE.invalidAuthorization().exception()
        }

        AccessToken accessToken = accessTokenRepository.get(tokens[1])
        if (accessToken == null) {
            throw AppExceptions.INSTANCE.invalidAuthorization().exception()
        }

        if (accessToken.isExpired()) {
            throw AppExceptions.INSTANCE.expiredAccessToken().exception()
        }

        return accessToken
    }
}
