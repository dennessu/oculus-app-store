/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.ApiService
import com.junbo.oauth.db.exception.DBUpdateConflictException
import com.junbo.oauth.db.repo.ApiDefinitionRepository
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * ApiServiceImpl.
 */
@CompileStatic
class ApiServiceImpl implements ApiService {

    private ApiDefinitionRepository apiDefinitionRepository

    @Required
    void setApiDefinitionRepository(ApiDefinitionRepository apiDefinitionRepository) {
        this.apiDefinitionRepository = apiDefinitionRepository
    }

    @Override
    ApiDefinition getApi(String apiName) {
        return apiDefinitionRepository.getApi(apiName)
    }

    @Override
    List<ApiDefinition> getAllApis() {
        return apiDefinitionRepository.allApis
    }

    @Override
    ApiDefinition saveApi(ApiDefinition apiDefinition) {

        ApiDefinition existingApi = apiDefinitionRepository.getApi(apiDefinition.apiName)

        if (existingApi != null) {
            throw AppExceptions.INSTANCE.duplicateEntityName('api', apiDefinition.apiName).exception()
        }

        return apiDefinitionRepository.saveApi(apiDefinition)
    }

    @Override
    ApiDefinition updateApi(String apiName, ApiDefinition apiDefinition) {

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
        } catch (DBUpdateConflictException ignore) {
            throw AppExceptions.INSTANCE.updateConflict().exception()
        }
    }

    @Override
    void deleteApi(String apiName) {

        ApiDefinition existingApi = apiDefinitionRepository.getApi(apiName)

        if (existingApi != null) {
            apiDefinitionRepository.deleteApi(existingApi)
        }
    }
}
