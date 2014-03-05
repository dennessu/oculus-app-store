package com.junbo.payment.app

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper
import com.junbo.payment.rest.jackson.JacksonFeature
import com.junbo.payment.rest.jackson.MapperConfigurator
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * Main class to start server.
 */
class BrainTreeMain {
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger")
        System.setProperty("logback.configurationFile", "logback-identity.xml")
        LOGGER = LoggerFactory.getLogger(Main)
    }
    private static final Logger LOGGER

    static HttpServer startServer() {

        def resourceConfig = new ResourceConfig()

        resourceConfig.register(JacksonFeature)
        resourceConfig.register(MapperConfigurator)
        resourceConfig.register(JsonMappingExceptionMapper)
        resourceConfig.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
        resourceConfig.property(ServerProperties.TRACING, 'ALL')
        resourceConfig.packages('com.junbo.payment.spec.internal.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        /*//TODO: change to async adapter later
        resourceConfig.packages("com.junbo.payment.rest.resource")
        resourceConfig.property("contextConfigLocation", "classpath*:/spring/*.xml")
        */
        def uri = URI.create('http://localhost:8081/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        def server = startServer()
        LOGGER.info('Server Started.')
        System.in.read()

        server.shutdown()
    }
}
