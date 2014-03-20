/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.app

import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.fulfilment.rest.exception.RestExceptionMapper
import com.junbo.fulfilment.rest.jackson.JacksonFeature
import com.junbo.fulfilment.rest.jackson.JsonMappingExceptionMapper
import com.junbo.fulfilment.rest.jackson.MapperConfigurator
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * Main
 */
class Main {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        resourceConfig
                .packages('com.junbo.fulfilment.spec.resource.adapter')
                .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
                .property(ServerProperties.TRACING, 'ALL')
                .property('contextConfigLocation', 'classpath*:/spring/*.xml')
                .register(MapperConfigurator)
                .register(JacksonFeature)
                .register(JsonMappingExceptionMapper)
                .register(RestExceptionMapper)
                .register(IdTypeFromStringProvider)

        def uri = URI.create('http://localhost:8090/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', 'logback-identity.xml')

        def server = startServer()
        System.in.read()
        server.shutdown()
    }
}
