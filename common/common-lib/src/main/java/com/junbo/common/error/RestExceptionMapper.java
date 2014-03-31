/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Javadoc.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {

        LOGGER.error("Log unhandled exception", exception);
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        if (exception instanceof JsonMappingException) {
            Error error = new Error("invalid_json", exception.getMessage(), null, null);
            return Response.status(Response.Status.BAD_REQUEST).entity(error).type(MediaType.APPLICATION_JSON).build();
        }

        LOGGER.error("Log unhandled exception", exception);

        Error error = new Error(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), null,
                Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), null);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                .type(MediaType.APPLICATION_JSON).build();
    }
}
