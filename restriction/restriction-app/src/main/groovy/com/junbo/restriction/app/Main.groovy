/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.app

import com.junbo.common.error.RestExceptionMapper
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.restriction.spec.provider.DateParamConverterProvider
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * App to launch Server
 */
class Main {

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        // jackson feature
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)

        resourceConfig.packages('com.junbo.restriction.spec.resource.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')

        resourceConfig.register(IdTypeFromStringProvider)
        resourceConfig.register(DateParamConverterProvider)
        resourceConfig.register(RestExceptionMapper)

        def uri = URI.create('http://localhost:8080/rest')
        GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        //Log comment, enable it when it is set

        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', 'logback-identity.xml')

        def server = startServer()
        System.in.read()

        server.shutdown()
    }
}