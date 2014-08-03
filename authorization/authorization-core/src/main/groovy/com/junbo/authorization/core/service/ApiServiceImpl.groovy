/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.service
import com.junbo.authorization.db.repository.ApiDefinitionRepository
import com.junbo.authorization.spec.model.ApiDefinition
import com.junbo.common.error.AppCommonErrors
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
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
        ApiDefinition api = apiDefinitionRepository.getApi(apiName)
        if (api == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound('api-definition', apiName).exception()
        }
        return api
    }
}
