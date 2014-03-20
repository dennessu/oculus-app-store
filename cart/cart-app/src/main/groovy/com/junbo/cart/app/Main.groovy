/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.app

import com.junbo.common.error.RestExceptionMapper
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.bridge.SLF4JBridgeHandler
import org.springframework.util.StringUtils

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
class Main {

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()


        resourceConfig.packages('com.junbo.cart.spec.resource.adapter', 'com.junbo.cart.rest.jackson',
                'com.junbo.cart.rest.filter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)
        resourceConfig.register(IdTypeFromStringProvider)
        resourceConfig.register(RestExceptionMapper)
        def uri = URI.create('http://localhost:8081/rest')
        if (!StringUtils.isEmpty(System.properties['cart.uri'])) {
            uri = URI.create(System.properties['cart.uri'])
        }
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
