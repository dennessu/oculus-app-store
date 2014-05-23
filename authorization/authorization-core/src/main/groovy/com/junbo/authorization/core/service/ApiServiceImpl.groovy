/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.service

import com.junbo.authorization.db.repository.ApiDefinitionRepository
import com.junbo.authorization.spec.error.AppErrors
import com.junbo.authorization.spec.model.ApiDefinition
import com.junbo.common.cloudant.exception.CloudantUpdateConflictException
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
    ApiDefinition saveApi(ApiDefinition apiDefinition) {

        ApiDefinition existingApi = apiDefinitionRepository.getApi(apiDefinition.apiName)

        if (existingApi != null) {
            throw AppErrors.INSTANCE.duplicateEntityName('api', apiDefinition.apiName).exception()
        }

        return apiDefinitionRepository.saveApi(apiDefinition)
    }

    @Override
    ApiDefinition updateApi(String apiName, ApiDefinition apiDefinition) {

        if (StringUtils.isEmpty(apiDefinition.revision)) {
            throw AppErrors.INSTANCE.missingRevision().exception()
        }

        if (apiName != apiDefinition.apiName) {
            throw AppErrors.INSTANCE.mismatchEntityName().exception()
        }

        ApiDefinition existingApi = apiDefinitionRepository.getApi(apiName)

        if (apiDefinition.revision != existingApi.revision) {
            throw AppErrors.INSTANCE.updateConflict('api_definition').exception()
        }

        try {
            apiDefinitionRepository.updateApi(apiDefinition)
        } catch (CloudantUpdateConflictException ignore) {
            throw AppErrors.INSTANCE.updateConflict('api_definition').exception()
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
