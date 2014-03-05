/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.mapper

import com.junbo.common.error.Error
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

/**
 * Javadoc.
 */
@CompileStatic
@Provider
class RestExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper)

    @Override
    Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).response
        }

        LOGGER.error('Log unhandled exception', exception)

        Error error = new Error(Response.Status.INTERNAL_SERVER_ERROR.reasonPhrase, null,
                Response.Status.INTERNAL_SERVER_ERROR.reasonPhrase, null)

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                .type(MediaType.APPLICATION_JSON).build()
    }
}
