/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.exceptionmapper;

import com.junbo.common.error.AppError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Error for Rest Layer.
 */
public class Error {
    Error(String code, String description, String field, List<Error> causes) {
        this.code = code;
        this.description = description;
        this.field = field;
        this.causes = causes;
    }

    private final String code;

    private final String description;

    private final String field;

    private final List<Error> causes;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getField() {
        return field;
    }

    public List<Error> getCauses() {
        return causes;
    }

    public static Response buildResponse(AppError error) {
        return Response.status(error.getHttpStatusCode()).entity(new Error(error.getCode(),
                error.getDescription(), error.getField(), null)).
                type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
