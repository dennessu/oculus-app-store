/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.bootstrap

import com.junbo.common.id.converter.IdTypeFromStringConverter
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Commerce bundle.
 */
@CompileStatic
class CommerceMain {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        // jackson feature
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)

        // present properties
        resourceConfig.register(com.junbo.configuration.ConfigResource)
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')

        // Id type feature
        resourceConfig.register(IdTypeFromStringConverter)
        resourceConfig.register(IdTypeFromStringProvider)

        // packages
        resourceConfig.packages('com.junbo.cart.spec.resource.adapter',
                'com.junbo.cart.rest.jackson', 'com.junbo.cart.rest.filter')

        def uri = URI.create('http://0.0.0.0:8082/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        Logger.getLogger('').setLevel(Level.ALL)
        for (Handler handler : Logger.getLogger('').handlers) {
            handler.setLevel(Level.ALL)
        }

        startServer()
    }
}
