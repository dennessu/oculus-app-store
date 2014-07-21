/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.configuration.crypto.CipherService
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * EncryptedClientRepositoryImpl.
 */
@CompileStatic
class EncryptedClientRepositoryImpl implements ClientRepository {
    private ClientRepository clientRepository

    private CipherService cipherService

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setCipherService(CipherService cipherService) {
        this.cipherService = cipherService
    }

    @Override
    Client getClient(String clientId) {
        Client client = clientRepository.getClient(clientId)
        if (client != null) {
            assert client.clientSecret != null
            client.clientSecret = cipherService.decrypt(client.clientSecret)
        }

        return client
    }

    @Override
    Client saveClient(Client client) {
        assert client != null
        String secret = client.clientSecret
        assert secret != null
        client.clientSecret = cipherService.encrypt(client.clientSecret)
        Client saved = clientRepository.saveClient(client)
        saved.clientSecret = secret
        return saved
    }

    @Override
    Client updateClient(Client client, Client oldClient) {
        assert client != null
        String secret = client.clientSecret
        assert secret != null
        client.clientSecret = cipherService.encrypt(secret)

        Client updated = clientRepository.updateClient(client, oldClient)
        updated.clientSecret = secret
        return updated
    }

    @Override
    void deleteClient(Client client) {
        clientRepository.deleteClient(client)
    }
}
