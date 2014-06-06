/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 409, code = "2000001", description = "Field {0} not writable.", field = "{0}")
    AppError fieldNotWritable(String field);

    @ErrorDef(httpStatusCode = 409, code = "2000002", description = "Field {0} invalid. Allowed values: {1}",
            field = "{0}")
    AppError fieldInvalid(String field, String allowedValues);

    @ErrorDef(httpStatusCode = 409, code = "2000003", description = "Field {0} invalid.", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 409, code = "2000004", description = "Field {0} too short. Min length is {1}",
            field = "{0}")
    AppError fieldTooShort(String field, Integer minLength);

    @ErrorDef(httpStatusCode = 409, code = "2000005", description = "Field {0} too long. Max length is {1}",
            field = "{0}")
    AppError fieldTooLong(String field, Integer maxLength);

    @ErrorDef(httpStatusCode = 409, code = "2000006", description = "Field {0} duplicate.", field = "{0}")
    AppError fieldDuplicate(String field);

    @ErrorDef(httpStatusCode = 400, code = "2000007", description = "Field {0} required.", field = "{0}")
    AppError fieldRequired(String field);

    @ErrorDef(httpStatusCode = 409, code = "2000010", description = "Param name {0} required.", field = "{0}")
    AppError parameterRequired(String paraName);

    @ErrorDef(httpStatusCode = 409, code = "2000013", description = "Param invalid due to {0}.", field = "{0}")
    AppError parameterInvalid(String message);

    @ErrorDef(httpStatusCode = 404, code = "2000035", description = "{0} {1} not found.", field = "{0}")
    AppError resourceNotFound(String resourceName, String id);

    @ErrorDef(httpStatusCode = 403, code = "2000037", description = "The operation is forbidden.")
    AppError forbidden();

    @ErrorDef(httpStatusCode = 409, code = "2000038", description = "The {0} name {1} already exists", field = "name")
    AppError duplicateEntityName(String entityName, String value);

    @ErrorDef(httpStatusCode = 400, code = "2000039", description = "The resourceAge is not provided",
            field = "resourceAge")
    AppError missingResourceAge();

    @ErrorDef(httpStatusCode = 400, code = "2000040", description = "The input name and entity name do not match")
    AppError mismatchEntityName();

    @ErrorDef(httpStatusCode = 409, code = "2000041",
            description = "The {0} is updated by someone else, please re-get the {0} and try update again",
            field = "{0}")
    AppError updateConflict(String resourceName);

    @ErrorDef(httpStatusCode = 403, code = "2000042",
            description = "The access token does not have sufficient scope to make the request",
            field = "access_token")
    AppError insufficientScope();
}
