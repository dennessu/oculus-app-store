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
import com.junbo.oauth.spec.endpoint.ApiEndpoint
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ApiEndpointImpl.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class ApiEndpointImpl implements ApiEndpoint {

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
    Promise<List<ApiDefinition>> getAllApis() {

        return tokenInfoParser.parseAndThen {
            if (!AuthorizeContext.hasScopes(API_INFO_SCOPE)) {
                return Promise.pure([])
            }

            return Promise.pure(apiService.allApis);
        }
    }

    @Override
    Promise<ApiDefinition> getApi(String apiName) {

        return tokenInfoParser.parseAndThen {
            if (!AuthorizeContext.hasScopes(API_INFO_SCOPE)) {
                return Promise.pure(null)
            }

            return Promise.pure(apiService.getApi(apiName));
        }
    }

    @Override
    Promise<ApiDefinition> postApi(ApiDefinition apiDefinition) {

        return tokenInfoParser.parseAndThen {
            if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
                throw AppExceptions.INSTANCE.insufficientScope().exception()
            }

            return Promise.pure(apiService.saveApi(apiDefinition));
        }
    }

    @Override
    Promise<ApiDefinition> putApi(String apiName, ApiDefinition apiDefinition) {

        return tokenInfoParser.parseAndThen {
            if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
                throw AppExceptions.INSTANCE.insufficientScope().exception()
            }

            return Promise.pure(apiService.updateApi(apiName, apiDefinition))
        }
    }

    @Override
    Promise<Void> deleteApi(String apiName) {

        return tokenInfoParser.parseAndThen {
            if (!AuthorizeContext.hasScopes(API_MANAGE_SCOPE)) {
                throw AppExceptions.INSTANCE.insufficientScope().exception()
            }

            apiService.deleteApi(apiName)

            return Promise.pure(null)
        }
    }
}
