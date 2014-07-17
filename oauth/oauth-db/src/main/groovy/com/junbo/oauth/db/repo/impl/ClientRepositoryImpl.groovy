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
    private ClientDAO clientDAO

    @Required
    void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO
    }

    @Override
    Client getClient(String clientId) {
        Assert.notNull(clientId)
        return wrap(clientDAO.get(clientId))
    }

    @Override
    Client saveClient(Client client) {
        return wrap(clientDAO.save(unwrap(client)))
    }

    @Override
    Client updateClient(Client client, Client oldClient) {
        return wrap(clientDAO.update(unwrap(client)))
    }

    @Override
    void deleteClient(Client client) {
        clientDAO.delete(unwrap(client))
    }

    private static ClientEntity unwrap(Client client) {
        if (client == null) {
            return null
        }

        return new ClientEntity(
                id: client.clientId,
                clientSecret: client.clientSecret,
                clientName: client.clientName,
                ownerUserId: client.ownerUserId,
                defaultRedirectUri: client.defaultRedirectUri,
                redirectUris: client.redirectUris,
                scopes: client.scopes,
                defaultScopes: client.defaultScopes,
                responseTypes: client.responseTypes,
                grantTypes: client.grantTypes,
                idTokenIssuer: client.idTokenIssuer,
                logoutRedirectUris: client.logoutRedirectUris,
                defaultLogoutRedirectUri: client.defaultLogoutRedirectUri,
                logoUri: client.logoUri,
                contacts: client.contacts,
                needConsent: client.needConsent,
                revision: client.rev
        )
    }

    private static Client wrap(ClientEntity entity) {
        if (entity == null) {
            return null
        }

        return new Client(
                clientId: entity.id,
                clientSecret: entity.clientSecret,
                clientName: entity.clientName,
                ownerUserId: entity.ownerUserId,
                defaultRedirectUri: entity.defaultRedirectUri,
                redirectUris: entity.redirectUris,
                scopes: entity.scopes,
                defaultScopes: entity.defaultScopes,
                responseTypes: entity.responseTypes,
                grantTypes: entity.grantTypes,
                idTokenIssuer: entity.idTokenIssuer,
                logoutRedirectUris: entity.logoutRedirectUris,
                defaultLogoutRedirectUri: entity.defaultLogoutRedirectUri,
                logoUri: entity.logoUri,
                contacts: entity.contacts,
                needConsent: entity.needConsent,
                rev: entity.revision
        )
    }
}
