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
import org.springframework.core.io.Resource

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
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        Client client
        try {
            client = transcoder.decode(new TypeReference<Client>() {}, content) as Client
        } catch (Exception e) {
            logger.error("Error parsing client $content", e)
            exit()
        }

        Client existing = clientRepository.getClient(client.clientId)

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite Client $client.clientId with this content.")
                client.clientId = existing.clientId
                client.rev = existing.rev
                try {
                    clientRepository.updateClient(client, existing)
                } catch (Exception e) {
                    logger.error("Error updating client $client.clientId.", e)
                }
            } else {
                logger.debug("The Client $client.clientId already exists, skip this content.")
            }
        } else {
            logger.debug("Create new Client $client.clientId with this content.")
            try {
                clientRepository.saveClient(client)
            } catch (Exception e) {
                logger.error("Error creating client $client.clientId.", e)
            }
        }
    }
}
