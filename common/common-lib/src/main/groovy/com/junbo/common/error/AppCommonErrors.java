/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

/**
 * Interface for CommonErrors.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppCommonErrors {
    AppCommonErrors INSTANCE = ErrorProxy.newProxyInstance(AppCommonErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "invalid Json: {1}")
    AppError invalidJson(String field, String detail);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Invalid identifier format: {0}")
    AppError invalidId(String field, String id);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Request body is required")
    AppError requestBodyRequired();

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error")
    AppError fieldInvalid(ErrorDetail[] details);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Field value is invalid.")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Field value is invalid. {1}")
    AppError fieldInvalid(String field, String message);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Field value is invalid. Allowed values: {1}")
    AppError fieldInvalidEnum(String field, String allowedValues);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Field too short. Min length is {1}")
    AppError fieldTooShort(String field, Integer minLength);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Field too long. Max length is {1}")
    AppError fieldTooLong(String field, Integer maxLength);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Field is required")
    AppError fieldRequired(String field);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Header is missing")
    AppError headerRequired(String header);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Field must be null")
    AppError fieldMustBeNull(String field);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Query parameter is required")
    AppError parameterRequired(String paraName);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Query parameter is invalid.")
    AppError parameterInvalid(String field);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "{0}", reason = "Query parameter is invalid. {1}")
    AppError parameterInvalid(String field, String message);

    @ErrorDef(httpStatusCode = 409, code = "002", message = "Input Validation Failure", field = "{0}", reason = "Field is not writable")
    AppError fieldNotWritable(String field);

    @ErrorDef(httpStatusCode = 409, code = "002", message = "Input Validation Failure", field = "{0}", reason = "Field is not writable. New Value: {1}, Expected value: {2}")
    AppError fieldNotWritable(String field, Object newValue, Object expectedValue);

    @ErrorDef(httpStatusCode = 409, code = "002", message = "Input Validation Failure", field = "{0}", reason = "Field value is duplicate")
    AppError fieldDuplicate(String field);

    @ErrorDef(httpStatusCode = 403, code = "003", message = "Resource Not Writable")
    AppError resourceNotWritable();

    @ErrorDef(httpStatusCode = 403, code = "003", message = "Forbidden")
    AppError forbidden();

    @ErrorDef(httpStatusCode = 403, code = "003", message = "Forbidden", reason = "{0}")
    AppError forbiddenWithMessage(String message);

    @ErrorDef(httpStatusCode = 403, code = "003", message = "Forbidden", field = "access_token", reason = "The access token is invalid")
    AppError invalidAccessToken();

    @ErrorDef(httpStatusCode = 403, code = "003", message = "Forbidden", field = "access_token", reason = "The access token does not have sufficient scope to make the request")
    AppError insufficientScope();

    @ErrorDef(httpStatusCode = 403, code = "003", message = "Forbidden", field = "access_token",
            reason = "The access token does not have sufficient scope to make the request to {0}")
    AppError insufficientScope(String resourceName);

    @ErrorDef(httpStatusCode = 404, code = "004", message = "Resource Not Found", field = "id", reason = "{0} {1} is not found")
    AppError resourceNotFound(String resourceName, Object id);

    @ErrorDef(httpStatusCode = 409, code = "005", message = "Resource Update Conflict", field = "rev", reason = "The entity {0} id={1}, rev={2} is out-dated")
    AppError updateConflict(String resourceName, String id, String rev);

    @ErrorDef(httpStatusCode = 412, code = "006", message = "Invalid Operation", field = "cause", reason = "{0}")
    AppError invalidOperation(String message);

    // 500 errors
    @ErrorDef(httpStatusCode = 500, code = "999", message = "Internal Server Error", field = "cause", reason = "{0}")
    AppError internalServerError(Exception e);

    @ErrorDef(httpStatusCode = 500, code = "999", message = "Internal Server Error")
    AppError internalServerError();
}
