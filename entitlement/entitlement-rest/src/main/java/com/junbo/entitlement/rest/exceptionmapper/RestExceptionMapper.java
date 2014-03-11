/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.rest.exceptionmapper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.junbo.entitlement.spec.error.AppErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Common exception mapper. Convert exceptions to json error message.
 */
public class RestExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof WebApplicationException) {    //common service exception
            return ((WebApplicationException) e).getResponse();
        } else if (e instanceof UnrecognizedPropertyException) {    //unnecessary field exception
            return AppErrors.INSTANCE.unnecessaryField(
                    ((UnrecognizedPropertyException) e).getUnrecognizedPropertyName()).exception().getResponse();
        } else if (e instanceof InvalidFormatException) {    //field invalid format exception
            return AppErrors.INSTANCE.fieldNotCorrect(
                    ((InvalidFormatException) e).getPathReference(), e.getMessage()).exception().getResponse();
        } else {    //other exceptions
            LOGGER.error("unCaught Exception", e);
            return AppErrors.INSTANCE.unCaught(e.getMessage()).exception().getResponse();
        }
    }
}
