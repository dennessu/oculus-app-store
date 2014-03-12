package com.junbo.token.app

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.token.rest.jackson.JacksonFeature
import com.junbo.token.rest.jackson.MapperConfigurator
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
class Main {
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()
        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', 'logback-identity.xml')
        LOGGER = LoggerFactory.getLogger(Main)
    }
    private static final Logger LOGGER

    static HttpServer startServer() {

        def resourceConfig = new ResourceConfig()

        resourceConfig.register(JacksonFeature)
        resourceConfig.register(MapperConfigurator)
        resourceConfig.register(JsonMappingExceptionMapper)
        //resourceConfig.register(RestExceptionMapper)
        resourceConfig.register(IdTypeFromStringProvider)
        resourceConfig.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
        resourceConfig.property(ServerProperties.TRACING, 'ALL')
        resourceConfig.packages('com.junbo.token.spec.resource.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        def uri = URI.create('http://localhost:8080/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        def server = startServer()
        LOGGER.info('Server Started.')
        System.in.read()

        server.shutdown()
    }
}
