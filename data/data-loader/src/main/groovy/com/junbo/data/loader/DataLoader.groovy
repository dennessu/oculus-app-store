/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.loader

import com.junbo.data.handler.DataHandler
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

/**
 * DataLoader.
 */
@CompileStatic
class DataLoader implements InitializingBean {
    private Map<String, DataHandler> handlers

    @Required
    void setHandlers(Map<String, DataHandler> handlers) {
        this.handlers = handlers
    }

    @Override
    void afterPropertiesSet() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.class.classLoader)
        Resource[] clients = resolver.getResources('data/client/*.data')

        for (Resource resource : clients) {
            DataHandler handler = handlers[getParentPath(resource.URI)]
            if (handler != null) {
                String content = IOUtils.toString(resource.URI)
                handler.handle(content)
            }
        }

        Resource[] resources = resolver.getResources('data/**/*.data')
        for (Resource resource : resources) {
            String parentPath = getParentPath(resource.URI)
            if (parentPath != 'client') {
                DataHandler handler = handlers[parentPath]
                if (handler != null) {
                    String content = IOUtils.toString(resource.URI)
                    handler.handle(content)
                }
            }
        }
    }

    private static String getParentPath(URI uri) {
        String[] tokens = uri.toString().split('/')
        if (tokens.length >= 2) {
            return tokens[tokens.length - 2]
        }
        return null
    }
}
