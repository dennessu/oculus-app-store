/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.mapper

import com.fasterxml.jackson.databind.JsonMappingException
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
 * The {@link javax.ws.rs.ext.ExceptionMapper} implementation in the OAuth application.
 * AppExceptionMapper will handle the WebApplicationException and return the corresponding http status code.
 * For JsonMappingException happened while deserializing the request, return BAD_REQUEST with invalid_json error.
 * For other unknown exceptions, log the exception and return INTERNAL_SERVER_ERROR.
 * @author Zhanxin Yang
 * @see javax.ws.rs.ext.ExceptionMapper
 */
@CompileStatic
@Provider
class AppExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppExceptionMapper)

    @Override
    Response toResponse(Exception exception) {

        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).response
        }

        if (exception instanceof JsonMappingException) {
            Error error = new Error('invalid_json', exception.message, null, null)

            return Response.status(Response.Status.BAD_GATEWAY).entity(error).type(MediaType.APPLICATION_JSON).build()
        }

        LOGGER.error('Log unhandled exception', exception)

        Error error = new Error(Response.Status.INTERNAL_SERVER_ERROR.reasonPhrase,
                Response.Status.INTERNAL_SERVER_ERROR.reasonPhrase, null, null)

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                .type(MediaType.APPLICATION_JSON).build()
    }
}
