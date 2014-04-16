/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import com.junbo.configuration.ConfigService;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Javadoc.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception>, ApplicationEventListener {

    @Autowired
    private ConfigService configService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        if ("true".equalsIgnoreCase(configService.getConfigValue("common.conf.debugMode"))) {
            return ERRORS.internalServerError(exception.getMessage(), exception).exception().getResponse();
        }
        return ERRORS.internalServerError().exception().getResponse();
    }

    @Override
    public void onEvent(ApplicationEvent event) {
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new RequestEventListener() {
            @Override
            public void onEvent(RequestEvent event) {
                if (event.getType() == RequestEvent.Type.ON_EXCEPTION) {
                    LOGGER.error("Log unhandled exception: ", event.getException());
                }
            }
        };
    }

    public static final Errors ERRORS = ErrorProxy.newProxyInstance(Errors.class);

    /**
     * Created by kg on 7/4/14.
     */
    public interface Errors {

        @ErrorDef(httpStatusCode = 500, code = "20001", description = "Internal Server Error : {0}. Exception : {1}.")
        AppError internalServerError(String message, Exception e);

        @ErrorDef(httpStatusCode = 500, code = "20001", description = "Internal Server Error.")
        AppError internalServerError();
    }
}
