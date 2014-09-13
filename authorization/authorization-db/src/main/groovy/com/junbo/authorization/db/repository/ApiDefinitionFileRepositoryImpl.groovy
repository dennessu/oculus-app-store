/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.db.repository

import com.junbo.authorization.spec.model.ApiDefinition
import com.junbo.langur.core.client.MessageTranscoder
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import javax.ws.rs.NotSupportedException

/**
 * ApiDefinitionFileRepositoryImpl.
 */
@CompileStatic
class ApiDefinitionFileRepositoryImpl implements ApiDefinitionRepository, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDefinitionFileRepositoryImpl)

    private Map<String, ApiDefinition> apiDefinitions = [:]

    private MessageTranscoder transcoder

    @Required
    void setTranscoder(MessageTranscoder transcoder) {
        this.transcoder = transcoder
    }

    @Override
    ApiDefinition getApi(String apiName) {
        return apiDefinitions[apiName]
    }

    @Override
    ApiDefinition saveApi(ApiDefinition api) {
        throw new NotSupportedException('create is not supported in file repository')
    }

    @Override
    ApiDefinition updateApi(ApiDefinition api, ApiDefinition oldApi) {
        throw new NotSupportedException('update is not supported in file repository')
    }

    @Override
    void afterPropertiesSet() throws Exception {
        def resolver = new PathMatchingResourcePatternResolver(this.class.classLoader)
        try {
            Resource[] resources = resolver.getResources("api-definition/*.data")

            for (Resource resource : resources) {
                String content = IOUtils.toString(resource.URI, 'UTF-8')
                def apiDefinition = transcoder.decode(new TypeReference<ApiDefinition>() {}, content) as ApiDefinition
                apiDefinitions[apiDefinition.apiName] = apiDefinition
            }
        } catch (IOException e) {
            LOGGER.error('Error loading the api definitions from configuration file', e)
        }
    }
}
