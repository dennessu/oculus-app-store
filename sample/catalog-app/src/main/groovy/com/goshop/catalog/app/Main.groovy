package com.goshop.catalog.app

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

/**
 * Created by Shenhua on 1/8/14.
 */
class Main {

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig();

        resourceConfig.packages("com.goshop.catalog.spec.resource.adapter")
        resourceConfig.property("contextConfigLocation", "classpath*:/spring/*.xml")

        def uri = URI.create("http://localhost:8081/rest")
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        def server = startServer()

        System.out.println("started\nHit enter to stop it...")
        System.in.read()

        server.shutdown()
    }
}
