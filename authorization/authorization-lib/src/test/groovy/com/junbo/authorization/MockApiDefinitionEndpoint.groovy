/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.ApiDefinitionEndpoint
import com.junbo.oauth.spec.model.MatrixRow
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic

import javax.ws.rs.PathParam

/**
 * MockApiDefinitionEndpoint.
 */
@CompileStatic
class MockApiDefinitionEndpoint implements ApiDefinitionEndpoint {
    private ApiDefinition api

    MockApiDefinitionEndpoint() {
        MatrixRow row1 = new MatrixRow(precondition: 'owner', rights: ['owner', 'read'])
        MatrixRow row2 = new MatrixRow(precondition: 'admin', rights: ['admin', 'read'])
        MatrixRow row3 = new MatrixRow(precondition: 'guest', rights: ['read'])
        api = new ApiDefinition(apiName: 'entity_get', scopes: ['entity': [row1, row2, row3]])
    }

    @Override
    Promise<List<ApiDefinition>> list() {
        return Promise.pure([api].asList())
    }

    @Override
    Promise<ApiDefinition> get(@PathParam("apiName") String apiName) {

        return Promise.pure(api)
    }

    @Override
    Promise<ApiDefinition> create(ApiDefinition apiDefinition) {
        return null
    }

    @Override
    Promise<ApiDefinition> update(String apiName, ApiDefinition apiDefinition) {
        return null
    }

    @Override
    Promise<Void> delete(String apiName) {
        return null
    }
}
