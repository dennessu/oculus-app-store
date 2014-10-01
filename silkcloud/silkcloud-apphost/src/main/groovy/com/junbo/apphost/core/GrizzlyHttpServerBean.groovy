package com.junbo.apphost.core

import com.junbo.apphost.core.logging.AccessLogProbe
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.*
import org.glassfish.grizzly.nio.transport.TCPNIOTransport
import org.glassfish.hk2.api.InjectionResolver
import org.glassfish.hk2.api.ServiceLocator
import org.glassfish.hk2.api.TypeLiteral
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer
import org.glassfish.jersey.internal.inject.Providers
import org.glassfish.jersey.server.ApplicationHandler
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.server.spi.ContainerProvider
import org.glassfish.jersey.server.wadl.WadlApplicationContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanNameAware
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.io.Resource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.ClassUtils

import java.util.concurrent.ExecutorService

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class GrizzlyHttpServerBean implements InitializingBean, DisposableBean,
        ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, BeanNameAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrizzlyHttpServerBean)

    private URI uri

    private ApplicationContext applicationContext

    private HttpServer httpServer

    private ServiceLocator serviceLocator

    private ExecutorService executorService

    private String beanName

    private int keepAliveIdleTimeoutInSeconds

    private int keepAliveMaxRequestsCount

    @Required
    void setUri(URI uri) {
        this.uri = uri
    }

    URI getUri() {
        return uri
    }

    @Required
    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    @Override
    void setBeanName(String name) {
        this.beanName = name
    }

    @Required
    void setKeepAliveIdleTimeoutInSeconds(int keepAliveIdleTimeoutInSeconds) {
        this.keepAliveIdleTimeoutInSeconds = keepAliveIdleTimeoutInSeconds
    }

    @Required
    void setKeepAliveMaxRequestsCount(int keepAliveMaxRequestsCount) {
        this.keepAliveMaxRequestsCount = keepAliveMaxRequestsCount
    }

    @Override
    void afterPropertiesSet() throws Exception {

        String host = (uri.host == null) ? NetworkListener.DEFAULT_NETWORK_HOST : uri.host
        int port = (uri.port == -1) ? 80 : uri.port

        httpServer = new HttpServer()

        // configure listener
        NetworkListener listener = new NetworkListener('grizzly', host, port)

        TCPNIOTransport transport = listener.transport
        transport.workerThreadPool = executorService

        listener.keepAlive.idleTimeoutInSeconds = keepAliveIdleTimeoutInSeconds
        listener.keepAlive.maxRequestsCount = keepAliveMaxRequestsCount

        httpServer.addListener(listener)

        def config = httpServer.serverConfiguration
        config.name = beanName

        // handle Jersey resources
        HttpHandler jerseyHandler = buildJerseyHandler()

        // handle static resources
        def staticResourceLoader = new CachedResourceClassLoader(ClassUtils.defaultClassLoader, 'static/')
        def staticHandler = new CLStaticHttpHandler(staticResourceLoader, []) {
            @Override
            protected void onMissingResource(Request request, Response response) throws Exception {
                jerseyHandler.service(request, response)
            }
        }

        config.addHttpHandler(staticHandler, uri.path)

        config.setPassTraceRequest(true)
        config.monitoringConfig.webServerConfig.addProbes(new AccessLogProbe())
    }

    private HttpHandler buildJerseyHandler() {
        // configure resourceConfig
        def resourceConfig = new ResourceConfig()

        readDistinctLines('classpath*:/jersey/components').each { String line ->
            LOGGER.info("Registering component class $line")

            def componentClass = ClassUtils.defaultClassLoader.loadClass(line)
            resourceConfig.register(componentClass)
        }

        readDistinctLines('classpath*:/jersey/packages').each { String line ->
            LOGGER.info("Registering package $line")

            resourceConfig.packages(line)
        }

        def customBinder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new AutowiredInjectResolver(applicationContext)).
                        to(new TypeLiteral<InjectionResolver<Autowired>>() { } )
            }
        }

        resourceConfig.addProperties(Collections.singletonMap(
                ServerProperties.WADL_FEATURE_DISABLE, (Object) true))

        // create handler
        ApplicationHandler applicationHandler = new ApplicationHandler(resourceConfig, customBinder)

        serviceLocator = applicationHandler.serviceLocator

        def wadlApplicationContext = (WadlApplicationContext) serviceLocator.getService(WadlApplicationContext)
        if (wadlApplicationContext != null) {
            wadlApplicationContext.wadlGenerationEnabled = false
        }

        HttpHandler handler = null
        for (ContainerProvider cp : Providers.getProviders(serviceLocator, ContainerProvider)) {
            handler = cp.createContainer(GrizzlyHttpContainer, applicationHandler)
            if (handler != null) {
                break
            }
        }

        if (handler == null) {
            throw new IllegalArgumentException('No container provider supports the GrizzlyHttpContainer')
        }

        return handler
    }

    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Starting [$beanName]...")
        httpServer.start()
    }

    @Override
    void destroy() throws Exception {
        LOGGER.info("Shutting down [$beanName]...")
        httpServer.shutdown()
    }

    ServiceLocator getServiceLocator() {
        return serviceLocator
    }

    private static Collection<String> readDistinctLines(String resourcePath) {
        def resourcePatternResolver = new PathMatchingResourcePatternResolver()

        return resourcePatternResolver.getResources(resourcePath).collectMany { Resource resource ->
            new EncodedResource(resource).inputStream.readLines()
        }.collect { String line -> line.trim() }.findAll { String line -> !line.empty }.unique()
    }
}
