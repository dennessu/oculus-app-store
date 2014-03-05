/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.exceptionmapper;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.junbo.entitlement.spec.error.AppErrors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Rest Layer ExceptionMapper for JsonMapping Exception.
 */
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    @Override
    public Response toResponse(JsonMappingException e) {
        if (e instanceof UnrecognizedPropertyException) {
            return Error.buildResponse(
                    AppErrors.INSTANCE.unnecessaryParameterField(
                            ((UnrecognizedPropertyException) e).getUnrecognizedPropertyName()));
        } else if (e instanceof InvalidFormatException) {
            return Error.buildResponse(
                    AppErrors.INSTANCE.fieldNotCorrect(
                            e.getPathReference(), e.getMessage()));
        }
        return null;
    }
}
