/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.UserId;

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

    @ErrorDef(httpStatusCode = 500, code = "5000007", description = "Key store exception: {0}.")
    AppError keyStoreException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000008", description = "Unrecoverable key exception: {0}.")
    AppError unrecoverableKeyException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000009", description = "IO exception: {0}.")
    AppError ioException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000010", description = "Certificate exception: {0}.")
    AppError certificateException(String message);

    @ErrorDef(httpStatusCode = 500, code = "5000011", description = "Field missing: {0}.", field = "{0}")
    AppError fieldMissing(String field);

    @ErrorDef(httpStatusCode = 500, code = "5000012", description = "User {0} not found.")
    AppError userNotFound(UserId userId);

    @ErrorDef(httpStatusCode = 500, code = "5000013", description = "Field invalid: {0}.")
    AppError fieldInvalid(String message);

    @ErrorDef(httpStatusCode = 403, code = "5000014", description = "Access denied")
    AppError accessDenied();

    @ErrorDef(httpStatusCode = 500, code = "5000015", description = "Signature exception: {0}.")
    AppError signatureException(String message);
}
