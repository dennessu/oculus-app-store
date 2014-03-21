/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.junbo.catalog.spec.error.AppErrors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Rest exception mapper.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {
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
            return AppErrors.INSTANCE.unCaught(e.getMessage()).exception().getResponse();
        }
    }
}
