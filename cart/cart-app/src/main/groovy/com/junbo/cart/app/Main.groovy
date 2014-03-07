/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.app

import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.util.StringUtils

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
class Main {

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        Logger.getLogger('').setLevel(Level.ALL)
        for (Handler handler : Logger.getLogger('').handlers) {
            handler.setLevel(Level.ALL)
        }

        resourceConfig.packages('com.junbo.cart.spec.resource.adapter', 'com.junbo.cart.rest.jackson',
                'com.junbo.cart.rest.filter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)
        resourceConfig.register(IdTypeFromStringProvider)
        def uri = URI.create('http://localhost:8081/rest')
        if (!StringUtils.isEmpty(System.properties['cart.uri'])) {
            uri = URI.create(System.properties['cart.uri'])
        }
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        def server = startServer()


        System.in.read()

        server.shutdown()
    }
}
