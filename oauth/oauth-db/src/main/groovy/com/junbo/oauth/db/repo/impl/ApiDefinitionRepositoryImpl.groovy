/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.impl

import com.junbo.oauth.db.dao.ApiDefinitionDAO
import com.junbo.oauth.db.entity.ApiDefinitionEntity
import com.junbo.oauth.db.repo.ApiDefinitionRepository
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ApiDefinitionRepositoryImpl.
 */
@CompileStatic
class ApiDefinitionRepositoryImpl implements ApiDefinitionRepository {
    private ApiDefinitionDAO apiDefinitionDAO

    @Required
    void setApiDefinitionDAO(ApiDefinitionDAO apiDefinitionDAO) {
        this.apiDefinitionDAO = apiDefinitionDAO
    }

    @Override
    ApiDefinition getApi(String apiName) {
        return wrap(apiDefinitionDAO.get(apiName))
    }

    @Override
    List<ApiDefinition> getAllApis() {
        return apiDefinitionDAO.all.collect { ApiDefinitionEntity entity ->
            return wrap(entity)
        }
    }

    @Override
    ApiDefinition saveApi(ApiDefinition api) {
        return wrap(apiDefinitionDAO.save(unwrap(api)))
    }

    @Override
    ApiDefinition updateApi(ApiDefinition api) {
        return wrap(apiDefinitionDAO.update(unwrap(api)))
    }

    @Override
    void deleteApi(ApiDefinition api) {
        apiDefinitionDAO.delete(unwrap(api))
    }

    private static ApiDefinitionEntity unwrap(ApiDefinition api) {
        if (api == null) {
            return null
        }

        return new ApiDefinitionEntity(
                id: api.apiName,
                scopes: api.scopes,
                revision: api.revision
        )
    }

    private static ApiDefinition wrap(ApiDefinitionEntity entity) {
        if (entity == null) {
            return null
        }

        return new ApiDefinition(
                apiName: entity.id,
                scopes: entity.scopes,
                revision: entity.revision
        )
    }
}
