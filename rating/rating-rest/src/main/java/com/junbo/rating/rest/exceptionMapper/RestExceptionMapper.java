/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.rest.exceptionMapper;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.junbo.common.error.Error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by lizwu on 3/25/14.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        } else if (exception instanceof JsonMappingException) {
            Error error = new Error("invalid_json", exception.getMessage(), null, null);
            return Response.status(Response.Status.BAD_REQUEST).entity(error).type(MediaType.APPLICATION_JSON).build();
        }

        Error error = new Error(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), null, null);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                .type(MediaType.APPLICATION_JSON).build();
    }
}
