package com.junbo.rating.app

import com.junbo.rating.rest.jackson.JacksonFeature
import com.junbo.rating.rest.jackson.MapperConfigurator
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
/**
 * App to launch Server
 */
class Main {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        resourceConfig.packages('com.junbo.rating.spec.resource.adapter')
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        resourceConfig.property(ServerProperties.TRACING, 'ALL')
        resourceConfig.register(MapperConfigurator)
        resourceConfig.register(JacksonFeature)

        def uri = URI.create('http://localhost:8080/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        def server = startServer()

        System.in.read()

        server.shutdown()
    }
}
