package com.junbo.rating.app

import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.rating.rest.exceptionMapper.RestExceptionMapper
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * App to launch Server.
 */
class Main {
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('logback.configurationFile', 'logback-identity.xml')
    }

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        resourceConfig.packages('com.junbo.rating.spec.resource.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        resourceConfig.property(ServerProperties.TRACING, 'ALL')
        resourceConfig.register(ObjectMapperProvider)
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(IdTypeFromStringProvider)
        resourceConfig.register(RestExceptionMapper)

        def uri = URI.create('http://localhost:8080/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        def server = startServer()

        System.in.read()

        server.shutdown()
    }
}
