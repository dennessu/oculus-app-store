/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.bootstrap

import com.junbo.common.error.RestExceptionMapper
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.provider.ResponseFilter
import com.junbo.configuration.ConfigResource
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.bridge.SLF4JBridgeHandler

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
        resourceConfig.register(ConfigResource)
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/**/*.xml')

        // Id type feature
        resourceConfig.register(IdTypeFromStringProvider)

        resourceConfig.register(RestExceptionMapper)
        // enable CROS
        resourceConfig.register(ResponseFilter)

        // packages
        resourceConfig.packages(
                'com.junbo.billing.spec.resource.adapter',
                'com.junbo.payment.spec.resource.adapter',
                'com.junbo.fulfilment.spec.resource.adapter',
                'com.junbo.entitlement.spec.resource.adapter',
                'com.junbo.rating.spec.resource.adapter',
                'com.junbo.cart.spec.resource.adapter',
                'com.junbo.order.spec.resource.adapter',
                'com.junbo.payment.spec.internal.adapter',
                'com.junbo.email.spec.resource.adapter',
                'com.junbo.ewallet.spec.resource.adapter')

        def uri = URI.create('http://0.0.0.0:8082/v1')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', './conf/logback-commerce-bundle.xml')

        startServer()
    }
}
