/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.rest.exception;

import com.junbo.fulfilment.common.exception.FulfilmentException;
import com.junbo.fulfilment.common.exception.ResourceNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * RestExceptionMapper.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<FulfilmentException> {
    private static final Map<Class, Response.Status> MAPPING;

    static {
        MAPPING = new HashMap();
        MAPPING.put(FulfilmentException.class, Response.Status.INTERNAL_SERVER_ERROR);
        MAPPING.put(ResourceNotFoundException.class, Response.Status.NOT_FOUND);
    }

    @Override
    public Response toResponse(FulfilmentException e) {
        Response.Status status = MAPPING.get(e.getClass());
        if (status == null) {
            status = Response.Status.BAD_REQUEST;
        }

        return Response
                .status(status)
                .entity(e.getMessage())
                .type("text/plain").build();
    }
}
