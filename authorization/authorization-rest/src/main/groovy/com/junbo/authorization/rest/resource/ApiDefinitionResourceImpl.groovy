/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.rest.resource

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
        def apiDefinition = apiService.getApi(apiName)

        if (apiDefinition == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound('api_definition', apiName).exception()
        }

        return Promise.pure(apiDefinition)
    }
}
