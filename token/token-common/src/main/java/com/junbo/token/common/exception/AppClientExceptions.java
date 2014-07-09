/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common.exception;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;


/**
 * Application Client Exceptions.
 */

public interface AppClientExceptions {
    AppClientExceptions INSTANCE = ErrorProxy.newProxyInstance(AppClientExceptions.class);

    @ErrorDef(httpStatusCode = 400, code = "40001", description = "The token is invalid", field = "token_id")
    AppError invalidToken();

    @ErrorDef(httpStatusCode = 404, code = "40401",
            description = "the resource {0} is not found", field = "resource")
    AppError resourceNotFound(String resource);

    @ErrorDef(httpStatusCode = 400, code = "40002",
            description = "the field {0} is not needed")
    AppError fieldNotNeeded(String field);

    @ErrorDef(httpStatusCode = 400, code = "40003",
            description = "the type {0} is not correct", field = "type")
    AppError invalidType(String type);

    @ErrorDef(httpStatusCode = 400, code = "40004",
            description = "the field {0} is missing")
    AppError missingField(String fieldName);

    @ErrorDef(httpStatusCode = 400, code = "40005",
            description = "the field {0} is invalid")
    AppError invalidField(String fieldName);

    @ErrorDef(httpStatusCode = 400, code = "40006", description = "The token status {0} is not activated")
    AppError invalidTokenStatus(String tokenStatus);

    @ErrorDef(httpStatusCode = 400, code = "40007", description = "The token is expired")
    AppError tokenExpired();

    @ErrorDef(httpStatusCode = 400, code = "40008", description = "exceed the token usage limitation")
    AppError exceedTokenUsageLimit();

    @ErrorDef(httpStatusCode = 400, code = "40008", description = "the product {0} is invalid")
    AppError invalidProduct(String product);
}
