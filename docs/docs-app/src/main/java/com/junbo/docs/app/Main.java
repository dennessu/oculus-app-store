/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app;

import com.wordnik.swagger.jaxrs.config.BeanConfig;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * The entry point of the docs app.
 */
public class Main {

    private static final int SERVER_PORT = 8079;
    private static final String CONTEXT_PATH = "api-docs";

    private Main() { }

    /**
     * The entry point of the docs app.
     */
    public static void main(String[] args) throws Exception {

        URI baseUri = UriBuilder.fromUri("http://0.0.0.0").port(SERVER_PORT).build();
        ResourceConfig config = new ResourceConfig();
        config.packages("com.wordnik.swagger.jersey.listing");
        config.files(true, "swagger-ui");

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setResourcePackage("com.junbo");

        beanConfig.setDescription("Oculus Commerce and Identity API Documentations");
        beanConfig.setTitle("Oculus Commerce and Identity API");
        beanConfig.setVersion("0.0.1-SNAPSHOT");
        beanConfig.setScan(true);

        // jersey container
        JettyHttpContainer apidocs = ContainerFactory.createContainer(JettyHttpContainer.class, config);

        // static files (swagger-ui)
        ResourceHandler swaggerui = new ResourceHandler();
        swaggerui.setDirectoriesListed(false);
        swaggerui.setWelcomeFiles(new String[]{"index.html"});
        swaggerui.setBaseResource(Resource.newClassPathResource("swagger-ui"));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { swaggerui, apidocs });

        Server server = new Server();
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        http.setPort(baseUri.getPort());
        server.setConnectors(new Connector[] { http });

        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
