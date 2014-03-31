/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.app

import com.junbo.common.error.RestExceptionMapper
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.InvalidJsonReaderInterceptor
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.filter.ResultsInterceptor
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
/**
 * App to launch Server
 */
class Main {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', 'logback-identity.xml')
        LOGGER = LoggerFactory.getLogger(Main)
    }

    private static final org.slf4j.Logger LOGGER

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        // jackson feature
        resourceConfig.packages('com.junbo.identity.spec.resource.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')

        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ResultsInterceptor)
        resourceConfig.register(IdTypeFromStringProvider)
        resourceConfig.register(InvalidJsonReaderInterceptor)
        resourceConfig.register(RestExceptionMapper)
        resourceConfig.register(ObjectMapperProvider)

        def uri = URI.create('http://0.0.0.0:8080/rest')
        GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        // Log comment, enable it when it is set

        def server = startServer()

        LOGGER.info('started\nHit enter to stop it...')
        System.in.read()

        server.shutdown()
    }
}
