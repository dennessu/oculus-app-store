/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * AppErrors.
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_NOT_FOUND,
            description ="User with id {0} not found")
    AppError invalidUserId(String userId);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.USER_STATUS_INVALID,
            description ="User status is invalid with id {0}")
    AppError invalidUserStatus(String userId);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.FIELD_MISSING_VALUE,
            description ="Field {0} has missing value")
    AppError fieldMissingValue(String field);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.FIELD_INVALID_VALUE,
            description ="Field {0} is invalid")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.PAYLOAD_IS_NULL,
            description ="The payload is null")
    AppError invalidPayload();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.TEMPLATE_NOT_FOUND,
            description = "Template {0} is not found")
    AppError templateNotFound(String template);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.PROPERTIES_INVALID,
            description = "The properties {0} is invalid")
    AppError invalidProperty(String property);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.EMAIL_SEND_ERROR,
            description = "Fail to send email, reason: {0}")
    AppError emailSendError(String reason);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.EMAIL_STATUS_INVALID,
            description = "Status {0} is invalid")
    AppError invalidStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.INTERNAL_ERROR,
            description = "Internal error, reason: {0}")
    AppError internalError(String reason);
}
