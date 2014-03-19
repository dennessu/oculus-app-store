/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.bootstrap

import com.junbo.common.error.RestExceptionMapper
import com.junbo.common.id.converter.IdTypeFromStringConverter
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.configuration.ConfigResource
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * Catalog bundle App.
 */
@CompileStatic
class CatalogMain {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')

        // jackson feature
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)

        // present properties - Get /properties
        resourceConfig.register(ConfigResource)

        // Id type feature
        resourceConfig.register(IdTypeFromStringConverter)
        resourceConfig.register(IdTypeFromStringProvider)

        resourceConfig.register(RestExceptionMapper)

        // packages
        resourceConfig.packages('com.junbo.catalog.spec.resource.adapter')

        def uri = URI.create('http://0.0.0.0:8083/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', 'logback-identity.xml')

        startServer()
        //System.out.println('started\nHit enter to stop it...')
        //System.in.read()
        //server.shutdown()
    }
}
