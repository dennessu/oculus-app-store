/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.ClientDAO
import com.junbo.oauth.db.entity.ClientEntity
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Javadoc.
 */
@CompileStatic
class ClientRepositoryImpl implements ClientRepository {
    private ClientDAO appClientDAO

    @Required
    void setAppClientDAO(ClientDAO appClientDAO) {
        this.appClientDAO = appClientDAO
    }

    @Override
    Client getClient(String clientId) {
        Assert.notNull(clientId)
        return wrap(appClientDAO.get(clientId))
    }

    @Override
    Client saveClient(Client client) {
        return wrap(appClientDAO.save(unwrap(client)))
    }

    @Override
    Client updateClient(Client client) {
        return wrap(appClientDAO.update(unwrap(client)))
    }

    @Override
    void deleteClient(Client client) {
        appClientDAO.delete(unwrap(client))
    }

    private static ClientEntity unwrap(Client client) {
        return new ClientEntity(
                id: client.clientId,
                clientSecret: client.clientSecret,
                clientName: client.clientName,
                defaultRedirectUri: client.defaultRedirectUri,
                redirectUris: client.redirectUris,
                scopes: client.scopes,
                defaultScopes: client.defaultScopes,
                responseTypes: client.responseTypes,
                grantTypes: client.grantTypes,
                idTokenIssuer: client.idTokenIssuer,
                logoutRedirectUris: client.logoutRedirectUris,
                defaultLogoutRedirectUri: client.defaultLogoutRedirectUri,
                logoUri: client.logoUri
        )
    }

    private static Client wrap(ClientEntity entity) {
        return new Client(
                clientId: entity.id,
                clientSecret: entity.clientSecret,
                clientName: entity.clientName,
                defaultRedirectUri: entity.defaultRedirectUri,
                redirectUris: entity.redirectUris,
                scopes: entity.scopes,
                defaultScopes: entity.defaultScopes,
                responseTypes: entity.responseTypes,
                grantTypes: entity.grantTypes,
                idTokenIssuer: entity.idTokenIssuer,
                logoutRedirectUris: entity.logoutRedirectUris,
                defaultLogoutRedirectUri: entity.defaultLogoutRedirectUri,
                logoUri: entity.logoUri
        )
    }
}
