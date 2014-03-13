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
 * Application Client Exceptions.
 */

public interface AppClientExceptions {
    AppClientExceptions INSTANCE = ErrorProxy.newProxyInstance(AppClientExceptions.class);

    @ErrorDef(httpStatusCode = 400, code = "40001", description = "The token {0} is invalid", field = "token_id")
    AppError invalidToken(String tokenString);

    @ErrorDef(httpStatusCode = 404, code = "40401",
            description = "the resource {0} is not found", field = "resource")
    AppError resourceNotFound(String resource);

}
