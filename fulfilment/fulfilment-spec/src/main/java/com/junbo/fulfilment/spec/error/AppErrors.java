/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.fulfilment.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Fulfilment AppError.
 */

public interface AppErrors {
    com.junbo.fulfilment.spec.error.AppErrors INSTANCE =
            ErrorProxy.newProxyInstance(com.junbo.fulfilment.spec.error.AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "30000", description = "{0}")
    AppError common(String msg);

    @ErrorDef(httpStatusCode = 400, code = "30001", description = "Missing Input field.", field = "{0}")
    AppError missingField(String field);

    @ErrorDef(httpStatusCode = 400, code = "30002", description = "Unnecessary field found.", field = "{0}")
    AppError unnecessaryField(String field);

    @ErrorDef(httpStatusCode = 404, code = "30003", description = "{0} [{1}] not found.")
    AppError notFound(String entity, Long id);

    @ErrorDef(httpStatusCode = 400, code = "30004", description = "Validation failed. {0}")
    AppError validation(String msg);

    @ErrorDef(httpStatusCode = 400, code = "30005", description = "Exception occurred during calling [{0}] component.")
    AppError gatewayFailure(String gateway);

    @ErrorDef(httpStatusCode = 500, code = "39999", description = "UnCaught Exception. {0}")
    AppError unCaught(String msg);
}
