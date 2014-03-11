/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */


package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.ClientEndpoint
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic

/**
 * ClientEndpointImpl.
 */
@CompileStatic
class ClientEndpointImpl implements ClientEndpoint {

    @Override
    Promise<Client> postClient(String authorization, Client client) {
        return null
    }

    @Override
    Promise<Client> getByClientId(String authorization, String clientId) {
        return null
    }

    @Override
    Promise<Client> putClient(String authorization, String clientId, Client client) {
        return null
    }

    @Override
    Promise<Client> resetSecret(String authorization, String clientId) {
        return null
    }
}
