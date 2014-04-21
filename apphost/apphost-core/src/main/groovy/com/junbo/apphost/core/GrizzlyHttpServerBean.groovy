package com.junbo.apphost.core

import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.HttpHandler
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.server.NetworkListener
import org.glassfish.grizzly.http.server.ServerConfiguration
import org.glassfish.hk2.api.ServiceLocator
import org.glassfish.hk2.utilities.ServiceLocatorUtilities
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer
import org.glassfish.jersey.internal.inject.Providers
import org.glassfish.jersey.server.ApplicationHandler
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spi.ContainerProvider
import org.springframework.beans.BeansException
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class GrizzlyHttpServerBean implements InitializingBean, DisposableBean, ApplicationContextAware {

    private URI uri

    private List<Class<?>> componentClasses

    private ApplicationContext applicationContext

    private HttpServer httpServer

    @Required
    void setUri(URI uri) {
        this.uri = uri
    }

    @Required
    void setComponentClasses(List<Class<?>> componentClasses) {
        this.componentClasses = componentClasses
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    @Override
    void afterPropertiesSet() throws Exception {

        String host = (uri.getHost() == null) ? NetworkListener.DEFAULT_NETWORK_HOST : uri.getHost()
        int port = (uri.getPort() == -1) ? 80 : uri.getPort()

        httpServer = new HttpServer()

        // configure listener
        NetworkListener listener = new NetworkListener("grizzly", host, port)
        httpServer.addListener(listener);

        // configure resourceConfig
        def resourceConfig = new ResourceConfig()

        for (Class<?> componentClass : componentClasses) {
            resourceConfig.register(componentClass)
        }

        for (ResourcePackagesProvider provider : applicationContext.getBeansOfType(ResourcePackagesProvider).values()) {
            resourceConfig.packages(provider.packages as String[])
        }

        // create handler
        ApplicationHandler applicationHandler = new ApplicationHandler(resourceConfig)
        ServiceLocator locator = applicationHandler.getServiceLocator()

        HttpHandler handler = null
        for (ContainerProvider cp : Providers.getProviders(locator, ContainerProvider)) {
            handler = cp.createContainer(GrizzlyHttpContainer, applicationHandler)
            if (handler != null) {
                break
            }
        }

        if (handler == null) {
            throw new IllegalArgumentException('No container provider supports the GrizzlyHttpContainer')
        }

        // configure serverConfiguration
        def ServerConfiguration config = httpServer.getServerConfiguration()

        config.addHttpHandler(handler, uri.getPath())
        config.setPassTraceRequest(true)

        ServiceLocatorUtilities.addOneConstant(locator, new AutowiredInjectResolver(applicationContext))

        // start
        httpServer.start()
    }

    @Override
    void destroy() throws Exception {

        httpServer.shutdown()
    }
}
