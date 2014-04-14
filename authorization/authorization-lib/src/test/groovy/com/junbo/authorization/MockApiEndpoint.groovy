/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.ApiEndpoint
import com.junbo.oauth.spec.model.ApiDefinition
import com.junbo.oauth.spec.model.MatrixRow
import groovy.transform.CompileStatic

import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

/**
 * MockApiEndpoint.
 */
@CompileStatic
class MockApiEndpoint implements ApiEndpoint {
    private ApiDefinition api

    MockApiEndpoint() {
        MatrixRow row1 = new MatrixRow(precondition: 'owner', rights: ['owner', 'read'])
        MatrixRow row2 = new MatrixRow(precondition: 'admin', rights: ['admin', 'read'])
        MatrixRow row3 = new MatrixRow(precondition: 'guest', rights: ['read'])
        api = new ApiDefinition(apiName: 'entity_get', scopes: ['entity': [row1, row2, row3]])
    }

    @Override
    Promise<List<ApiDefinition>> getAllApis(String authorization) {
        return Promise.pure([api].asList())
    }

    @Override
    Promise<ApiDefinition> getApi(String authorization,
                                  @PathParam("apiName") String apiName) {

        return Promise.pure(api)
    }

    @Override
    Promise<ApiDefinition> postApi(String authorization, ApiDefinition apiDefinition) {
        return null
    }

    @Override
    Promise<ApiDefinition> putApi(String authorization, String apiName, ApiDefinition apiDefinition) {
        return null
    }

    @Override
    Promise<Response> deleteApi(String authorization, String apiName) {
        return null
    }
}
