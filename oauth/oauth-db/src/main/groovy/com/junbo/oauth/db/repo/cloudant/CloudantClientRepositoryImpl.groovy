/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.option.PageableGetOptions
import groovy.transform.CompileStatic
import org.springframework.util.Assert

/**
 * CloudantClientRepositoryImpl.
 */
@CompileStatic
class CloudantClientRepositoryImpl extends CloudantClient<Client> implements ClientRepository {
    @Override
    List<Client> getAllClients(PageableGetOptions options) {
        assert options != null : 'options is null'
        options.total = cloudantGetAllSync(Integer.MAX_VALUE, 0, false).size()
        return cloudantGetAllSync(options.validCount, options.validCursor, false)
    }

    @Override
    Client getClient(String clientId) {
        Assert.notNull(clientId)
        return cloudantGetSync(clientId)
    }

    @Override
    Client saveClient(Client client) {
        return cloudantPostSync(client)
    }

    @Override
    Client updateClient(Client client, Client oldClient) {
        return cloudantPutSync(client, oldClient)
    }

    @Override
    void deleteClient(Client client) {
        cloudantDeleteSync(client.clientId)
    }
}
