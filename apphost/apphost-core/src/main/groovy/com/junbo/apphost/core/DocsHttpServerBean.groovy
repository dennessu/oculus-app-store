package com.junbo.apphost.core
import com.junbo.apphost.core.logging.AccessLogProbe
import com.junbo.configuration.ConfigServiceManager
import com.wordnik.swagger.jaxrs.config.BeanConfig
import com.wordnik.swagger.jersey.JerseyApiReader
import com.wordnik.swagger.reader.ClassReaders
import com.wordnik.swagger.reader.ModelReader
import com.wordnik.swagger.reader.ModelReaders
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.CLStaticHttpHandler
import org.glassfish.grizzly.http.server.HttpHandler
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.server.NetworkListener
import org.glassfish.grizzly.http.server.Request
import org.glassfish.grizzly.http.server.Response
import org.glassfish.grizzly.nio.transport.TCPNIOTransport
import org.glassfish.hk2.api.ServiceLocator
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer
import org.glassfish.jersey.internal.inject.Providers
import org.glassfish.jersey.server.ApplicationHandler
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spi.ContainerProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanNameAware
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.util.ClassUtils

import java.util.concurrent.ExecutorService
/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class DocsHttpServerBean implements InitializingBean, DisposableBean,
        ApplicationListener<ContextRefreshedEvent>, BeanNameAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocsHttpServerBean)

    private URI uri

    private HttpServer httpServer

    private ServiceLocator serviceLocator

    private ExecutorService executorService

    private String beanName

    private int keepAliveIdleTimeoutInSeconds

    private int keepAliveMaxRequestsCount

    private ModelReader modelReader;

    private JerseyApiReader apiReader;

    @Required
    void setUri(URI uri) {
        this.uri = uri
    }

    @Required
    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService
    }

    @Required
    void setModelReader(ModelReader modelReader) {
        this.modelReader = modelReader
    }

    @Required
    void setApiReader(JerseyApiReader apiReader) {
        this.apiReader = apiReader
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
        initializeSwagger()

        String host = (uri.host == null) ? NetworkListener.DEFAULT_NETWORK_HOST : uri.host
        int port = (uri.port == -1) ? 80 : uri.port

        httpServer = new HttpServer()

        // configure listener
        NetworkListener listener = new NetworkListener('docs', host, port)

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
        def staticResourceLoader = new CachedResourceClassLoader(ClassUtils.defaultClassLoader, 'swagger-ui/')
        def staticHandler = new CLStaticHttpHandler(staticResourceLoader, []) {
            @Override
            protected void onMissingResource(Request request, Response response) throws Exception {
                String requestUri = request.getRequestURI().replaceAll(/^\/*/, "").replaceAll(/\/*$/, "")
                if (requestUri == "" || requestUri == "swagger-ui") {
                    response.setStatus(302)
                    response.addHeader("Location", "/swagger-ui/index.html")
                    return;
                }
                jerseyHandler.service(request, response)
            }
        }

        config.addHttpHandler(staticHandler, uri.path)

        config.setPassTraceRequest(true)
        config.monitoringConfig.webServerConfig.addProbes(new AccessLogProbe())
    }

    private HttpHandler buildJerseyHandler() {
        ResourceConfig config = new ResourceConfig();
        config.packages("com.wordnik.swagger.jersey.listing");

        // create handler
        ApplicationHandler applicationHandler = new ApplicationHandler(config)

        serviceLocator = applicationHandler.serviceLocator

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

    private void initializeSwagger() {
        BeanConfig beanConfig = new BeanConfig();

        ModelReaders.setReader(modelReader);
        ClassReaders.setReader(apiReader);

        beanConfig.setResourcePackage("com.junbo");

        String basePath = ConfigServiceManager.instance().getConfigValue("oauth.core.auth.linkBaseUri")
        beanConfig.setDescription("Oculus Commerce and Identity API Documentations");
        beanConfig.setTitle("Oculus Commerce and Identity API");
        beanConfig.setVersion(JunboApplication.package.implementationVersion);
        beanConfig.setBasePath(basePath);
        beanConfig.setScan(true);
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
}
