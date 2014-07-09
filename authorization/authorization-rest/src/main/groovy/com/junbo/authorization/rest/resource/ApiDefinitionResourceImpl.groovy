/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.rest.resource
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.TokenInfoParser
import com.junbo.authorization.core.service.ApiService
import com.junbo.authorization.spec.model.ApiDefinition
import com.junbo.authorization.spec.resource.ApiDefinitionResource
import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * ApiDefinitionResourceImpl.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class ApiDefinitionResourceImpl implements ApiDefinitionResource {

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
    Promise<ApiDefinition> get(String apiName) {

        if (!AuthorizeContext.hasScopes(API_INFO_SCOPE) && !AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.resourceNotFound('api_definition', apiName).exception()
        }

        def apiDefinition = apiService.getApi(apiName)

        if (apiDefinition == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound('api_definition', apiName).exception()
        }

        return Promise.pure(apiDefinition)
    }

    @Override
    Promise<ApiDefinition> create(ApiDefinition apiDefinition) {

        if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception()
        }

        return Promise.pure(apiService.saveApi(apiDefinition))
    }

    @Override
    Promise<ApiDefinition> update(String apiName, ApiDefinition apiDefinition) {

        if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception()
        }

        return Promise.pure(apiService.updateApi(apiName, apiDefinition))
    }

    @Override
    Promise<Void> delete(String apiName) {

        if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception()
        }

        apiService.deleteApi(apiName)

        return Promise.pure(null)
    }
}
