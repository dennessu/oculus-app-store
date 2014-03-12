/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.token.core.exception;


import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Application Server Exceptions.
 */
public interface AppServerExceptions {

    AppServerExceptions INSTANCE = ErrorProxy.newProxyInstance(AppServerExceptions.class);

    @ErrorDef(httpStatusCode = 500, code = "50002",
            description = "The token status {0} is invalid to process")
    AppError invalidTokenStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = "50003",
            description = "The token set status {0} is invalid to process")
    AppError invalidTokenSetStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = "50004",
            description = "The token order status {0} is invalid to process")
    AppError invalidTokenOrderStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = "50005", description = "The required field {0} is missing while processing")
    AppError missingRequiredField(String field);
}
