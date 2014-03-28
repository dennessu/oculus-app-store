package com.junbo.order.app
import com.junbo.common.error.RestExceptionMapper
import com.junbo.common.id.provider.IdTypeFromStringProvider
import com.junbo.common.json.JacksonFeature
import com.junbo.common.json.ObjectMapperProvider
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.bridge.SLF4JBridgeHandler
/**
 * Created by chriszhu on 24/1/2014.
 */
@CompileStatic
@TypeChecked
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
        resourceConfig.register(RestExceptionMapper)

        def uri = URI.create('http://localhost:8080/rest')
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
