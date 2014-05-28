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
import org.springframework.context.annotation.Scope

import javax.ws.rs.core.Response

/**
 * Default {@link com.junbo.oauth.spec.endpoint.ClientEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.ClientEndpoint
 */
@CompileStatic
@Scope('prototype')
class ClientEndpointImpl implements ClientEndpoint {
    /**
     * The ClientService to do the actual work.
     */
    private ClientService clientService

    @Required
    void setClientService(ClientService clientService) {
        this.clientService = clientService
    }

    /**
     * Endpoint to create a client.
     * The requester should provide an access token that has 'client.manage' scope.
     * @param client The request body of the to-be-created Client.
     * @return The created client.
     */
    @Override
    Promise<Client> postClient(Client client) {
        return Promise.pure(clientService.saveClient(client))
    }

    /**
     * Endpoint to retrieve an client with the client id.
     * This endpoint will return the full client information, so the requester must be the client owner.
     * The requester should provide an access token that has 'client.manage' scope and the client owner as the user id.
     * @param clientId The client id to be retrieved.
     * @return The full client information of the given client id.
     */
    @Override
    Promise<Client> getByClientId(String clientId) {
        return Promise.pure(clientService.getClient(clientId))
    }

    /**
     * Endpoint to retrieve the basic information of the client.
     * The requester should provide an access token that has 'client.info' scope.
     * @param clientId The client id to be retrieved.
     * @return The basic client information of the given client id (client id, client scope, etc.).
     */
    @Override
    Promise<Client> getInfoByClientId(String clientId) {
        return Promise.pure(clientService.getClientInfo(clientId))
    }

    /**
     * Endpoint to update an existing client.
     * The requester must be the client owner.
     * The requester should provide an access token that has 'client.manage' scope and the client owner as the user id.
     * @param clientId The client id to be updated.
     * @param client The request body of the client to be updated.
     * @return The full client information of the given client id (updated version).
     */
    @Override
    Promise<Client> putClient(String clientId, Client client) {
        return Promise.pure(clientService.updateClient(clientId, client))
    }

    /**
     * Endpoint to delete an existing client.
     * The requester must be the client owner.
     * The requester should provide an access token that has 'client.manage' scope and the client owner as the user id.
     * @param clientId The client id to be deleted.
     * @return The raw javax.ws.rs Response (Mostly response with code of 204 No content).
     */
    @Override
    Promise<Response> deleteClient(String clientId) {
        clientService.deleteClient(clientId)
        return Promise.pure(Response.noContent().build())
    }

    /**
     * Endpoint to reset the client secret.
     * The requester must be the client owner.
     * The requester should provide an access token that has 'client.manage' scope and the client owner as the user id.
     * @param clientId The client id to be reset client secret.
     * @return The full client information of the given client id with the updated client secret.
     */
    @Override
    Promise<Client> resetSecret(String clientId) {
        return Promise.pure(clientService.resetSecret(clientId))
    }
}
