/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface ClientRepository {
    Client getClient(String clientId)

    Client saveClient(Client client)

    Client updateClient(Client client, Client oldClient)

    void deleteClient(Client client)
}