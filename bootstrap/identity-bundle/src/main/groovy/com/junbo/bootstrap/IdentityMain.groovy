/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.bootstrap

import com.junbo.common.error.RestExceptionMapper
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.InvalidJsonReaderInterceptor
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.provider.ResponseFilter
import com.junbo.configuration.ConfigResource
import com.junbo.identity.spec.filter.ResultsInterceptor
import com.junbo.sharding.view.EntityListener
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.bridge.SLF4JBridgeHandler

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
        resourceConfig.register(ConfigResource)
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/**/*.xml')

        // packages
        resourceConfig.packages('com.junbo.identity.spec.v1.resource.adapter')
        resourceConfig.packages('com.junbo.oauth.spec.endpoint.adapter')

        resourceConfig.register(IdTypeFromStringProvider)
        resourceConfig.register(RestExceptionMapper)
        resourceConfig.register(ResultsInterceptor)
        resourceConfig.register(InvalidJsonReaderInterceptor)
        resourceConfig.register(EntityListener)
        resourceConfig.register(ResponseFilter)

        def uri = URI.create('http://0.0.0.0:8081/v1')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', './conf/logback-identity-bundle.xml')


        startServer()
    }
}
