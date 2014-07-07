/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.util.Assert

/**
 * CloudantClientRepositoryImpl.
 */
@CompileStatic
class CloudantClientRepositoryImpl extends CloudantClient<Client> implements ClientRepository {
    @Override
    Client getClient(String clientId) {
        Assert.notNull(clientId)
        return cloudantGet(clientId).get()
    }

    @Override
    Client saveClient(Client client) {
        return cloudantPost(client).get()
    }

    @Override
    Client updateClient(Client client) {
        return cloudantPut(client).get()
    }

    @Override
    void deleteClient(Client client) {
        cloudantDelete(client.clientId).get()
    }
}
