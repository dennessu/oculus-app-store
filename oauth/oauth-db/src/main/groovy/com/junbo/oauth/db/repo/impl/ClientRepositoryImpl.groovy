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

//    private static ClientEntity unwrap(Client client) {
//        return new ClientEntity(
//                clientId: client.clientId,
//                clientSecret: client.clientSecret,
//                defaultRedirectUri: client.defaultRedirectUri,
//                allowedRedirectUris: client.allowedRedirectUris,
//                allowedScopes: client.allowedScopes
//        )
//    }

    private static Client wrap(ClientEntity entity) {
        return new Client(
                clientId: entity.clientId,
                clientSecret: entity.clientSecret,
                defaultRedirectUri: entity.defaultRedirectUri,
                allowedRedirectUris: entity.allowedRedirectUris,
                allowedScopes: entity.allowedScopes,
                defaultScopes: entity.defaultScopes,
                allowedResponseTypes: entity.allowedResponseTypes,
                idTokenIssuer: entity.idTokenIssuer,
                allowedLogoutRedirectUris: entity.allowedLogoutRedirectUris,
                defaultLogoutRedirectUri: entity.defaultLogoutRedirectUri
        )
    }
}
