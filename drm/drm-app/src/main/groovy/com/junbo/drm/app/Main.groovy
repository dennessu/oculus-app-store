package com.junbo.drm.app

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

/**
 * Created by NanXin on 14-1-20.
 */
class Main {
    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

//        resourceConfig.packages("com.junbo.catalog.spec.resource.adapter")
        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')

        def uri = URI.create('http://localhost:8080/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri)
    }

    static void main(String[] args) {

        def server = startServer()

        //LOGGER.info('Server Started.')
        System.in.read()

        server.shutdown()
    }
}
