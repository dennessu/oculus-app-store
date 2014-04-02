/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.ApiEndpoint
import com.junbo.oauth.spec.model.ApiDefinition
import com.junbo.oauth.spec.model.AuthorizePolicy
import groovy.transform.CompileStatic

import javax.ws.rs.HeaderParam
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

/**
 * MockApiEndpoint.
 */
@CompileStatic
class MockApiEndpoint implements ApiEndpoint {
    @Override
    Promise<List<ApiDefinition>> getAllApis(String authorization) {
        AuthorizePolicy policy = new AuthorizePolicy(claims: ['owner':['owner', 'read'].toSet(),
                                                              'admin':['admin', 'read'].toSet(),
                                                              'guest':['read'].toSet()])
        ApiDefinition api = new ApiDefinition(apiName: 'entity_get', authorizePolicies: ['entity':policy])
        return Promise.pure([api].toList())
    }

    @Override
    Promise<ApiDefinition> getApi( String authorization,
            @PathParam("apiName") String apiName) {
        return null
    }

    @Override
    Promise<ApiDefinition> postApi(String authorization, ApiDefinition apiDefinition) {
        return null
    }

    @Override
    Promise<ApiDefinition> putApi(String authorization,  String apiName, ApiDefinition apiDefinition) {
        return null
    }

    @Override
    Promise<Response> deleteApi(String authorization,  String apiName) {
        return null
    }
}
