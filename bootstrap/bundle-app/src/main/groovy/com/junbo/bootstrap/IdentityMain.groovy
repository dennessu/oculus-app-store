package com.junbo.bootstrap

import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.oauth.api.mapper.RestExceptionMapper
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class IdentityMain {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        // jackson feature
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)

        // identity
        resourceConfig.packages('com.junbo.identity.spec.resource.adapter')
        resourceConfig.property("contextConfigLocation", "classpath*:/spring/*.xml")

        // oauth
        resourceConfig.packages("com.junbo.oauth.spec.endpoint.adapter")
        resourceConfig.register(RestExceptionMapper.class)

        def uri = URI.create("http://0.0.0.0:8080/identity")
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {
        Logger.getLogger('').setLevel(Level.ALL)
        for (Handler handler : Logger.getLogger('').handlers) {
            handler.setLevel(Level.ALL)
        }

        def server = startServer()

        System.out.println("started\nHit enter to stop it...")
        System.in.read()
        server.shutdown()
    }
}
