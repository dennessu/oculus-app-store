/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.file

import com.junbo.langur.core.client.MessageTranscoder
import com.junbo.langur.core.client.TypeReference
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.Scope
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
 * ScopeFileRepositoryImpl.
 */
@CompileStatic
class ScopeFileRepositoryImpl implements ScopeRepository, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeFileRepositoryImpl)

    private Map<String, Scope> scopes = [:]

    private MessageTranscoder transcoder

    @Required
    void setTranscoder(MessageTranscoder transcoder) {
        this.transcoder = transcoder
    }

    @Override
    Scope getScope(String name) {
        return scopes[name]
    }

    @Override
    Scope saveScope(Scope scope) {
        throw new NotSupportedException()
    }

    @Override
    Scope updateScope(Scope scope, Scope oldScope) {
        throw new NotSupportedException()
    }

    @Override
    void deleteScope(Scope scope) {
        throw new NotSupportedException()
    }

    @Override
    void afterPropertiesSet() throws Exception {
        def resolver = new PathMatchingResourcePatternResolver(this.class.classLoader)
        try {
            Resource[] resources = resolver.getResources("data/scope/*.data")

            for (Resource resource : resources) {
                String content = IOUtils.toString(resource.URI, 'UTF-8')
                def scope = transcoder.decode(new TypeReference<Scope>() {}, content) as Scope
                scopes[scope.name] = scope
            }
        } catch (IOException e) {
            LOGGER.error('Error loading the scopes from configuration file', e)
        }
    }
}
