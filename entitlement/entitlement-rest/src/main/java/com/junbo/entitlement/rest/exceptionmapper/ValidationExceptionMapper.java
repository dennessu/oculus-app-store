/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.exceptionmapper;

import com.junbo.entitlement.spec.error.AppErrors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Rest Layer ExceptionMapper for Validation Exception.
 */
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException e) {
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) e;
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation cv : ex.getConstraintViolations()) {
                sb.append(cv.getPropertyPath()).append(" ")
                        .append(cv.getMessage()).append(". ");
            }
            return Error.buildResponse(AppErrors.INSTANCE.validation(sb.toString()));
        } else {
            return Error.buildResponse(AppErrors.INSTANCE.validation(e.getMessage()));
        }
    }
}
