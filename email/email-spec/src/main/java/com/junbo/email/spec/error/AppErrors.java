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


    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_EMAIL_ID,
            description ="The email id {0} is invalid")
    AppError invalidEmailId(String id);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_NOT_FOUND,
            description ="User {0} not found", field = "user")
    AppError invalidUserId(String userId);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_EMAIL_NOT_FOUND,
            description ="User email address not found")
    AppError emptyUserEmail();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_USER_STATUS,
            description ="User status is invalid with id {0}")
    AppError invalidUserStatus(String userId);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.EMAIL_SCHEDULE_NOT_FOUND,
            description ="Email schedule {0} not found", field = "id")
    AppError emailScheduleNotFound(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.MISSING_FIELD_VALUE,
            description ="Field {0} has missing value", field = "{0}")
    AppError missingField(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_FIELD_VALUE,
            description ="Field {0} is invalid", field = "{0}")
    AppError invalidField(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.UNNECESSARY_FILED,
            description ="Field {0} is unnecessary", field = "{0}")
    AppError unnecessaryField(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_PARAMETER,
            description ="Parameter {0} is invalid")
    AppError invalidParameter(String param);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.PAYLOAD_IS_NULL,
            description ="The payload is null")
    AppError invalidPayload();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.EMAIL_TEMPLATE_NOT_FOUND,
            description = "Template is not found")
    AppError templateNotFound();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.TEMPLATE_NAME_ALREADY_EXIST,
            description ="Template with specified source, action and locale is already exists")
    AppError emailTemplateAlreadyExist();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_PLACEHOLDERNAMES,
            description ="The placeholderNames filed missing subject placeholder",
            field = "placeholderNames")
    AppError invalidPlaceholderNames();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_REPLACEMENTS,
            description = "The {0} is invalid", field = "replacements")
    AppError invalidReplacements(String replacement);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.NO_VALIDATED_USER_EMAIL,
            description = "There is no validated default email in this user")
    AppError noValidatedUserEmail();

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.EMAIL_SEND_ERROR,
            description = "Fail to send email, reason: {0}")
    AppError emailSendError(String reason);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.EMAIL_STATUS_INVALID,
            description = "Status {0} is invalid")
    AppError invalidStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.INTERNAL_ERROR,
            description = "Internal error, reason: {0}")
    AppError internalError(String reason);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.USER_EMAIL_FETCH_ERROR,
            description = "Failed to fetch user email address")
    AppError fetchUserEmailError();
}
