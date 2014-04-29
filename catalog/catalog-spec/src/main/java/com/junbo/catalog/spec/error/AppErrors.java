/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * Copied from identity.
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "10000", description = "Missing Input field.", field = "{0}")
    AppError missingField(String field);

    @ErrorDef(httpStatusCode = 400, code = "10001", description = "Unnecessary field found.", field = "{0}")
    AppError unnecessaryField(String field);

    @ErrorDef(httpStatusCode = 403, code = "10002",
            description = "{0} does not match. actually: {1}, expected: {2}.",
            field = "{0}")
    AppError fieldNotMatch(String fieldName, Object actually, Object expected);

    @ErrorDef(httpStatusCode = 404, code = "10003", description = "{0} [{1}] not found.")
    AppError notFound(String entity, String id);

    @ErrorDef(httpStatusCode = 400, code = "10004", description = "invalid Json: {0}")
    AppError invalidJson(String detail);

    @ErrorDef(httpStatusCode = 400, code = "10005", description = "Field is not correct. {1}", field = "{0}")
    AppError fieldNotCorrect(String fieldName, String msg);

    @ErrorDef(httpStatusCode = 400, code = "10007", description = "Validation failed. {0}")
    AppError validation(String msg);

    @ErrorDef(httpStatusCode = 400, code = "10011", description = "Validation failed.")
    AppError validation(AppError[] errors);

    @ErrorDef(httpStatusCode = 400, code = "10008", description = "{0}")
    AppError common(String msg);

    @ErrorDef(httpStatusCode = 500, code = "10009", description = "UnCaught Exception. {0}")
    AppError unCaught(String msg);

    @ErrorDef(httpStatusCode = 409, code = "10010", description = "Access Denied")
    AppError accessDenied();
}
