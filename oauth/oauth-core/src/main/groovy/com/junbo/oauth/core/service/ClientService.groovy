/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic

/**
 * ClientService.
 */
@CompileStatic
interface ClientService {
    Client postClient(String authorization, Client client)

    Client getClient(String authorization, String clientId)

    Client updateClient(String authorization, String clientId, Client client)

    void deleteClient(String authorization, String clientId)

    Client resetSecret(String authorization, String clientId)
}
