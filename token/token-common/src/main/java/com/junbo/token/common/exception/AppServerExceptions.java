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
 * Application Server Exceptions.
 */
public interface AppServerExceptions {

    AppServerExceptions INSTANCE = ErrorProxy.newProxyInstance(AppServerExceptions.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Token Order Not Found",
            field = "tokenOrder", reason = "Token Order with ID {0} is not found")
    AppError tokenOrderNotFound(String tokenOrderId);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Token Set Not Found",
            field = "tokenSetId", reason = "Token Set with ID {0} is not found")
    AppError tokenSetNotFound(String tokenSetId);

    @ErrorDef(httpStatusCode = 500, code = "103", message = "Catalog Gateway Error")
    AppError catalogGatewayException();
}
