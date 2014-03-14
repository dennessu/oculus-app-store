/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.model.GrantType
import com.junbo.oauth.spec.model.ResponseType
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class HackClientRepo implements ClientRepository {
    private final Client client

    HackClientRepo() {
        client = new Client(
                clientId: 'client',
                clientSecret: 'secret',
                defaultRedirectUri: 'http://localhost'
        )

        Set<String> redirectUris = []
        redirectUris.add('http://localhost')
        redirectUris.add('*')
        client.setRedirectUris(redirectUris)

        Set<String> scopes = []
        scopes.add('openid')
        scopes.add('identity')
        scopes.add('client.register')
        client.setScopes(scopes)

        Set<ResponseType> responseTypes = []
        responseTypes.add(ResponseType.CODE)
        responseTypes.add(ResponseType.ID_TOKEN)
        responseTypes.add(ResponseType.TOKEN)
        client.setResponseTypes(responseTypes)

        Set<GrantType> grantTypes = [GrantType.AUTHORIZATION_CODE, GrantType.PASSWORD, GrantType.REFRESH_TOKEN].toSet()

        client.grantTypes = grantTypes

        client.idTokenIssuer = 'www.junbo.com'

        Set<String> logoutRedirectUris = []
        logoutRedirectUris.add('http://localhost')
        client.logoutRedirectUris = logoutRedirectUris

        client.defaultLogoutRedirectUri = 'http://localhost'

        client.ownerUserId = 762
    }

    @Override
    Client getClient(String clientId) {
        if (clientId == client.clientId) {
            return client
        }
        return null
    }

    @Override
    Client saveClient(Client client) {
        return null
    }

    @Override
    Client updateClient(Client client) {
        return null
    }

    @Override
    void deleteClient(Client client) {

    }
}
