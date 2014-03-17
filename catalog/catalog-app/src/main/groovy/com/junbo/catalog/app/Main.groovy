/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.app

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper
import com.junbo.catalog.rest.exception.RestExceptionMapper
import com.junbo.catalog.rest.jackson.JacksonFeature
import com.junbo.catalog.rest.jackson.MapperConfigurator
import com.junbo.common.id.provider.IdTypeFromStringProvider
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Main class to start server.
 */
class Main {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        resourceConfig.register(JacksonFeature)
        resourceConfig.register(MapperConfigurator)
        resourceConfig.register(JsonMappingExceptionMapper)
        resourceConfig.register(RestExceptionMapper)
        resourceConfig.register(IdTypeFromStringProvider)

        resourceConfig.packages('com.junbo.catalog.spec.resource.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        resourceConfig.property(ServerProperties.TRACING, 'ALL')
        resourceConfig.property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true)

        def uri = URI.create('http://localhost:8091/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        Logger.getLogger('').setLevel(Level.ALL)
        for (Handler handler : Logger.getLogger('').handlers) {
            handler.setLevel(Level.ALL)
        }

        def server = startServer()

        System.in.read()

        server.shutdown()
    }
}
