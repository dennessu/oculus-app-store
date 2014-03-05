/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.exception;

import com.junbo.catalog.common.exception.CatalogException;
import com.junbo.catalog.common.exception.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest exception mapper.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<CatalogException> {
    private static final Map<Class, Response.Status> MAPPING;

    static {
        MAPPING = new HashMap<>();
        MAPPING.put(CatalogException.class, Response.Status.INTERNAL_SERVER_ERROR);
        MAPPING.put(NotFoundException.class, Response.Status.NOT_FOUND);
    }

    @Override
    public Response toResponse(CatalogException e) {
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
