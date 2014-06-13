/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler

import com.junbo.langur.core.client.TypeReference
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ClientDataHandler.
 */
@CompileStatic
class ClientDataHandler extends BaseDataHandler {
    private ClientRepository clientRepository

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Override
    void handle(String content) {
        Client client

        try {
            client = transcoder.decode(new TypeReference<Client>() {}, content) as Client
        } catch (Exception e) {
            logger.warn('Error parsing Client, skip this content', e)
            return
        }

        Client existing = clientRepository.getClient(client.clientId)

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite Client $client.clientId with this content.")
                client.clientId = existing.clientId
                client.revision = existing.revision
                clientRepository.updateClient(client)
            } else {
                logger.debug("The Client $client.clientId already exists, skip this content.")
            }
        } else {
            logger.debug("Create new Client $client.clientId with this content.")
            clientRepository.saveClient(client)
        }
    }
}
