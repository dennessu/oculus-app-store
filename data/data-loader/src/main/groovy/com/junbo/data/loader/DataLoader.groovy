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
    private List<String> dataList

    @Required
    void setHandlers(Map<String, DataHandler> handlers) {
        this.handlers = handlers
    }

    @Required
    void setDataList(List<String> dataList) {
        this.dataList = dataList
    }

    @Override
    void afterPropertiesSet() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.class.classLoader)

        for (String data : dataList) {
            Resource[] resources = resolver.getResources("data/$data/*.data")
            for (Resource resource : resources) {
                DataHandler handler = handlers[data]
                if (handler != null) {
                    String content = IOUtils.toString(resource.URI)
                    handler.handle(content)
                }
            }
        }
    }
}
