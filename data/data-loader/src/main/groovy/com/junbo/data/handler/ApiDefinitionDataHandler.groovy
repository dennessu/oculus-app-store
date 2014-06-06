/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler

import com.junbo.authorization.spec.model.ApiDefinition
import com.junbo.authorization.spec.resource.ApiDefinitionResource
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ApiDefinitionDataHandler.
 */
@CompileStatic
class ApiDefinitionDataHandler extends BaseDataHandler {
    private ApiDefinitionResource apiDefinitionResource

    @Required
    void setApiDefinitionResource(ApiDefinitionResource apiDefinitionResource) {
        this.apiDefinitionResource = apiDefinitionResource
    }

    @Override
    void handle(String content) {
        ApiDefinition apiDefinition

        try {
            apiDefinition = transcoder.decode(new TypeReference<ApiDefinition>() {}, content) as ApiDefinition
        } catch (Exception e) {
            logger.warn('Error parsing ApiDefinition, skip this content', e)
            return
        }

        ApiDefinition existing = null
        try {
            existing = apiDefinitionResource.get(apiDefinition.apiName).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite ApiDefinition $apiDefinition.apiName with this content.")
                apiDefinition.id = existing.id
                apiDefinition.rev = existing.rev
                apiDefinitionResource.update(apiDefinition.apiName, apiDefinition).get()
            }
            else {
                logger.debug("ApiDefinition $apiDefinition.apiName already exists, skip this content.")
            }
        } else {
            logger.debug("Create new ApiDefinition $apiDefinition.apiName with this content.")
            apiDefinitionResource.create(apiDefinition)
        }
    }
}
