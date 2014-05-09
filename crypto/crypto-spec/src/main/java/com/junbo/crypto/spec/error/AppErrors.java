/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 500, code = "5000000", description = "Internal error: {0}")
    AppError internalError(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000001", description = "Algorithm is not found: {0}.")
    AppError noSuchAlgorithmException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000002", description = "Padding is not found: {0}.")
    AppError noSuchPaddingException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000003", description = "Invalid key: {0}.")
    AppError invalidKeyException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000004", description = "Invalid algorithm parameter: {0}.")
    AppError invalidAlgorithmParameterException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000005", description = "Illegal block size: {0}.")
    AppError illegalBlockSizeException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000006", description = "Bad padding: {0}.")
    AppError badPaddingException(String message);
}
