/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.bootstrap

import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.filter.ResultListInterceptor
import groovy.transform.CompileStatic

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Identity bundle.
 */
@CompileStatic
class IdentityMain {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        // jackson feature
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)

        // present properties
        resourceConfig.register(com.junbo.configuration.ConfigResource)
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')

        // packages
        resourceConfig.packages('com.junbo.identity.spec.resource.adapter')
        resourceConfig.packages('com.junbo.oauth.spec.endpoint.adapter')

        // filters
        resourceConfig.register(ResultListInterceptor)

        def uri = URI.create('http://0.0.0.0:8081/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        Logger.getLogger('').setLevel(Level.ALL)
        for (Handler handler : Logger.getLogger('').handlers) {
            handler.setLevel(Level.ALL)
        }

        startServer()

        //System.out.println('started\nHit enter to stop it...')
        //System.in.read()
        //server.shutdown()
    }
}
