/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.service.ClientService
import com.junbo.oauth.spec.endpoint.ClientEndpoint
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.Response

/**
 * ClientEndpointImpl.
 */
@CompileStatic
class ClientEndpointImpl implements ClientEndpoint {
    private ClientService clientService

    @Required
    void setClientService(ClientService clientService) {
        this.clientService = clientService
    }

    @Override
    Promise<Client> postClient(String authorization, Client client) {
        return Promise.pure(clientService.saveClient(authorization, client))
    }

    @Override
    Promise<Client> getByClientId(String authorization, String clientId) {
        return Promise.pure(clientService.getClient(authorization, clientId))
    }

    @Override
    Promise<Client> getInfoByClientId(String authorization, String clientId) {
        return Promise.pure(clientService.getClientInfo(authorization, clientId))
    }

    @Override
    Promise<Client> putClient(String authorization, String clientId, Client client) {
        return Promise.pure(clientService.updateClient(authorization, clientId, client))
    }

    @Override
    Promise<Response> deleteClient(String authorization, String clientId) {
        clientService.deleteClient(authorization, clientId)
        return Promise.pure(Response.noContent().build())
    }

    @Override
    Promise<Client> resetSecret(String authorization, String clientId) {
        return Promise.pure(clientService.resetSecret(authorization, clientId))
    }
}
