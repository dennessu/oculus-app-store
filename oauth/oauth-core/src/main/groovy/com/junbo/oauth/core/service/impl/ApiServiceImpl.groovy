/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.ApiService
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.junbo.oauth.db.repo.ApiDefinitionRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * ApiServiceImpl.
 */
@CompileStatic
class ApiServiceImpl implements ApiService {
    private static final String API_MANAGE_SCOPE = 'api.manage'
    private static final String API_INFO_SCOPE = 'api.info'
    private ApiDefinitionRepository apiDefinitionRepository
    private TokenService tokenService

    @Required
    void setApiDefinitionRepository(ApiDefinitionRepository apiDefinitionRepository) {
        this.apiDefinitionRepository = apiDefinitionRepository
    }

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    ApiDefinition getApi(String authorization, String apiName) {
        validateAccessToken(authorization, [API_MANAGE_SCOPE, API_INFO_SCOPE].toSet())

        return apiDefinitionRepository.getApi(apiName)
    }

    @Override
    List<ApiDefinition> getAllApis(String authorization) {
        validateAccessToken(authorization, [API_MANAGE_SCOPE, API_INFO_SCOPE].toSet())

        return apiDefinitionRepository.allApis
    }

    @Override
    ApiDefinition saveApi(String authorization, ApiDefinition apiDefinition) {
        validateAccessToken(authorization, [API_MANAGE_SCOPE].toSet())

        ApiDefinition existingApi = apiDefinitionRepository.getApi(apiDefinition.apiName)

        if (existingApi != null) {
            throw AppExceptions.INSTANCE.duplicateEntityName('api', apiDefinition.apiName).exception()
        }

        return apiDefinitionRepository.saveApi(apiDefinition)
    }

    @Override
    ApiDefinition updateApi(String authorization, String apiName, ApiDefinition apiDefinition) {
        validateAccessToken(authorization, [API_MANAGE_SCOPE].toSet())

        if (StringUtils.isEmpty(apiDefinition.revision)) {
            throw AppExceptions.INSTANCE.missingRevision().exception()
        }

        if (apiName != apiDefinition.apiName) {
            throw AppExceptions.INSTANCE.mismatchEntityName().exception()
        }

        ApiDefinition existingApi = apiDefinitionRepository.getApi(apiName)

        if (apiDefinition.revision != existingApi.revision) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }

        try {
            apiDefinitionRepository.updateApi(apiDefinition)
        } catch (DBUpdateConflictException e) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }
    }

    @Override
    void deleteApi(String authorization, String apiName) {
        validateAccessToken(authorization, [API_MANAGE_SCOPE].toSet())

        ApiDefinition existingApi = apiDefinitionRepository.getApi(apiName)
        if (existingApi != null) {
            apiDefinitionRepository.deleteApi(existingApi)
        }
    }

    private void validateAccessToken(String authorization, Set<String> scopes) {
        AccessToken accessToken = tokenService.extractAccessToken(authorization)

        boolean hasValidScope = scopes.any { String scope ->
            accessToken.scopes.contains(scope)
        }

        if (!hasValidScope) {
            throw AppExceptions.INSTANCE.insufficientScope().exception()
        }
    }
}
