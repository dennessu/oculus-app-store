/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.repo.AppClientRepository
import com.junbo.oauth.spec.model.AppClient
import com.junbo.oauth.spec.model.ResponseType
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class HackAppClientRepo implements AppClientRepository {
    private final AppClient appClient

    HackAppClientRepo() {
        appClient = new AppClient(
                clientId: 'client',
                clientSecret: 'secret',
                defaultRedirectUri: 'http://localhost'
        )

        Set<String> redirectUris = []
        redirectUris.add('http://localhost')
        redirectUris.add('*')
        appClient.setAllowedRedirectUris(redirectUris)

        Set<String> scopes = []
        scopes.add('openid')
        scopes.add('identity')
        appClient.setAllowedScopes(scopes)

        Set<ResponseType> responseTypes = []
        responseTypes.add(ResponseType.CODE)
        responseTypes.add(ResponseType.ID_TOKEN)
        responseTypes.add(ResponseType.TOKEN)
        appClient.setAllowedResponseTypes(responseTypes)

        appClient.idTokenIssuer = 'www.junbo.com'
    }

    @Override
    AppClient getAppClient(String clientId) {
        if (clientId == appClient.clientId) {
            return appClient
        }
        return null
    }
}
