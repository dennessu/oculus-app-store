package com.junbo.order.app

import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger
/**
 * Created by chriszhu on 24/1/2014.
 */
@CompileStatic
class Main {

    static HttpServer startServer() {
        def resourceConfig = new ResourceConfig()

        resourceConfig.packages(
                'com.junbo.order.spec.resource.adapter',
                'com.junbo.identity.spec.resource.adapter',
                'com.junbo.billing.spec.resource.adapter',
                'com.junbo.catalog.spec.resource.adapter',
                'com.junbo.rating.spec.resource.adapter',
                'com.junbo.fulfilment.spec.resource.adapter',
                'com.junbo.payment.spec.resource.adapter',
        )

        resourceConfig.property('contextConfigLocation', 'classpath*:/spring/*.xml')
        resourceConfig.register(JacksonFeature)
        resourceConfig.register(ObjectMapperProvider)
        resourceConfig.register(IdTypeFromStringProvider)

        def uri = URI.create('http://localhost:8080/rest')
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig)
    }

    static void main(String[] args) {

        Logger.getLogger('').level = Level.ALL
        Logger.getLogger('').handlers.each { Handler it ->
            it.level = Level.ALL
        }

        def server = startServer()
        System.in.read()

        server.shutdown()
    }
}
