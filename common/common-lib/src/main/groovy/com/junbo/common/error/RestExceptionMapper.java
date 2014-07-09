/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Javadoc.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception>, ApplicationEventListener {

    private static ConfigService configService = ConfigServiceManager.instance();
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {

        if (e instanceof WebApplicationException) {
            return ((WebApplicationException)e).getResponse();
        } else if (e instanceof UnrecognizedPropertyException) {    // unnecessary field exception
            return AppCommonErrors.INSTANCE.invalidJson(
                    ((UnrecognizedPropertyException) e).getUnrecognizedPropertyName(), "The property is unrecognized").exception().getResponse();
        } else if (e instanceof InvalidFormatException) {           // field invalid format exception
            return AppCommonErrors.INSTANCE.fieldInvalid(
                    ((InvalidFormatException) e).getPathReference(), e.getMessage()).exception().getResponse();
        } else if (e instanceof JsonParseException) {
            return AppCommonErrors.INSTANCE.invalidJson(
                    "request.body", e.getMessage()).exception().getResponse();
        } else if (e instanceof JsonMappingException) {
            return AppCommonErrors.INSTANCE.fieldInvalid(
                    ((JsonMappingException) e).getPathReference(), e.getMessage()).exception().getResponse();
        }

        // other exceptions
        if ("true".equalsIgnoreCase(configService.getConfigValue("common.conf.debugMode"))) {
            return AppCommonErrors.INSTANCE.internalServerError(e).exception().getResponse();
        }
        return AppCommonErrors.INSTANCE.internalServerError().exception().getResponse();
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
}
