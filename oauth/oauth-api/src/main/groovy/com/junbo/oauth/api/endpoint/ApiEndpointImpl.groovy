/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.service.ApiService
import com.junbo.oauth.spec.endpoint.ApiEndpoint
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.Response

/**
 * ApiEndpointImpl.
 */
@CompileStatic
class ApiEndpointImpl implements ApiEndpoint {
    private ApiService apiService

    @Required
    void setApiService(ApiService apiService) {
        this.apiService = apiService
    }

    @Override
    Promise<List<ApiDefinition>> getAllApis(String authorization) {
        return Promise.pure(apiService.getAllApis(authorization))
    }

    @Override
    Promise<ApiDefinition> getApi(String authorization, String apiName) {
        return Promise.pure(apiService.getApi(authorization, apiName))
    }

    @Override
    Promise<ApiDefinition> postApi(String authorization, ApiDefinition apiDefinition) {
        return Promise.pure(apiService.createApi(authorization, apiDefinition))
    }

    @Override
    Promise<ApiDefinition> putApi(String authorization, String apiName, ApiDefinition apiDefinition) {
        return Promise.pure(apiService.updateApi(authorization, apiName, apiDefinition))
    }

    @Override
    Promise<Response> deleteApi(String authorization, String apiName) {
        apiService.deleteApi(authorization, apiName)
        return Promise.pure(Response.noContent().build())
    }
}
