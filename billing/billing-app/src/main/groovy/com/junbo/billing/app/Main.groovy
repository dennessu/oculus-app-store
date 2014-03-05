/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.app

import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by xmchen on 1/16/14.
 */
class Main {

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        resourceConfig.packages('com.junbo.billing.spec.resource.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')

        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)

        def uri = URI.create('http://localhost:8080/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        Logger.getLogger('').level = Level.ALL
        Logger.getLogger('').handlers.each { Handler it ->
            it.level = Level.ALL
        }

        def server = startServer()
        System.in.read()

        server.shutdown()
    }
}
