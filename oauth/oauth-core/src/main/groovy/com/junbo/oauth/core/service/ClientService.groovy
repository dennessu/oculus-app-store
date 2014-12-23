/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.option.PageableGetOptions
import groovy.transform.CompileStatic

/**
 * ClientService.
 */
@CompileStatic
interface ClientService {
    List<Client> getAllClients(PageableGetOptions options)

    Client saveClient(Client client)

    Client getClient(String clientId)

    Client getClientInfo(String clientId)

    Client updateClient(String clientId, Client client)

    void deleteClient(String clientId)

    Client resetSecret(String clientId)
}
