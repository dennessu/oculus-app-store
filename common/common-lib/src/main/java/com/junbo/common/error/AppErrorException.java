/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Interface for AppError.
 */

public class AppErrorException extends WebApplicationException {
    private final AppError error;

    public AppError getError() {
        return error;
    }

    public AppErrorException(AppError error) {
        super(error.toString(), Response.status(error.getHttpStatusCode()).entity(error.error()).
                type(MediaType.APPLICATION_JSON_TYPE).build());

        this.error = error;
    }
}
