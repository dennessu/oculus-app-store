/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.TokenInfoParser
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.ApiService
import com.junbo.oauth.spec.endpoint.ApiDefinitionEndpoint
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ApiDefinitionEndpointImpl.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class ApiDefinitionEndpointImpl implements ApiDefinitionEndpoint {

    private static final String API_MANAGE_SCOPE = 'api.manage'

    private static final String API_INFO_SCOPE = 'api.info'

    private ApiService apiService

    private TokenInfoParser tokenInfoParser

    @Required
    void setApiService(ApiService apiService) {
        this.apiService = apiService
    }

    @Required
    void setTokenInfoParser(TokenInfoParser tokenInfoParser) {
        this.tokenInfoParser = tokenInfoParser
    }

    @Override
    Promise<List<ApiDefinition>> list() {

        if (!AuthorizeContext.hasScopes(API_INFO_SCOPE)) {
            return Promise.pure([])
        }

        return Promise.pure(apiService.allApis)
    }

    @Override
    Promise<ApiDefinition> get(String apiName) {

        if (!AuthorizeContext.hasScopes(API_INFO_SCOPE)) {
            throw AppExceptions.INSTANCE.apiDefinitionNotFound(apiName).exception()
        }

        def apiDefinition = apiService.getApi(apiName)

        if (apiDefinition == null) {
            throw AppExceptions.INSTANCE.apiDefinitionNotFound(apiName).exception()
        }

        return Promise.pure(apiDefinition)
    }

    @Override
    Promise<ApiDefinition> create(ApiDefinition apiDefinition) {

        if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        return Promise.pure(apiService.saveApi(apiDefinition))
    }

    @Override
    Promise<ApiDefinition> update(String apiName, ApiDefinition apiDefinition) {

        if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        return Promise.pure(apiService.updateApi(apiName, apiDefinition))
    }

    @Override
    Promise<Void> delete(String apiName) {

        if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }

        apiService.deleteApi(apiName)

        return Promise.pure(null)
    }
}
