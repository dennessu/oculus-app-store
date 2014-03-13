/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.core.exception;


import com.junbo.common.error.*;
import com.junbo.common.error.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Exceptions Mapper to handle the unhandled exceptions.
 */
public class RestExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        LOGGER.error("Log unhandled exception", exception);

        com.junbo.common.error.Error error = new Error(String.valueOf(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                , null,Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), null);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                .type(MediaType.APPLICATION_JSON).build();
    }
}
