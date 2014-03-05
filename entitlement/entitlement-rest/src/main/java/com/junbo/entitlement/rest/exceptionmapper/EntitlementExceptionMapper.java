/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.exceptionmapper;

import com.junbo.entitlement.common.exception.EntitlementException;
import com.junbo.entitlement.spec.error.AppErrors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Rest Layer ExceptionMapper for Entitlement Exception.
 */
@Provider
public class EntitlementExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException e) {
        if (e instanceof EntitlementException) {
            return Error.buildResponse(
                    AppErrors.ENTITLEMENT_EXCEPTION_REST_MAP.get(e.getClass()).apply((EntitlementException) e));
        } else {
            return Error.buildResponse(AppErrors.INSTANCE.unCaught(e.getMessage()));
        }
    }
}
