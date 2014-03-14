/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.rest.mapper

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.junbo.ewallet.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

/**
 * Common exception mapper. Convert exceptions to json error message.
 */
@CompileStatic
class RestExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper)

    @Override
    Response toResponse(Exception e) {
        if (e instanceof WebApplicationException) {    //common service exception
            return ((WebApplicationException) e).response
        } else if (e instanceof UnrecognizedPropertyException) {    //unnecessary field exception
            return AppErrors.INSTANCE.unnecessaryField(
                    ((UnrecognizedPropertyException) e).unrecognizedPropertyName).exception().response
        } else if (e instanceof InvalidFormatException) {    //field invalid format exception
            return AppErrors.INSTANCE.fieldNotCorrect(
                    ((InvalidFormatException) e).pathReference, e.message).exception().response
        }      //other exceptions
        LOGGER.error('unCaught Exception', e)
        return AppErrors.INSTANCE.unCaught(e.message).exception().response
    }
}
