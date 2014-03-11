/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.common.exception;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface of AppExceptions.
 */
public interface AppExceptions {

    AppExceptions INSTANCE = ErrorProxy.newProxyInstance(AppExceptions.class);

    @ErrorDef(httpStatusCode = 400, code = "40001", description = "The user_id {0} is invalid", field = "user_id")
    AppError invalidUserId(String userId);

    @ErrorDef(httpStatusCode = 400, code = "40002", description = "The user_id is missing", field = "user_id")
    AppError missingUserId();

    @ErrorDef(httpStatusCode = 400, code = "40003", description = "The payload is null")
    AppError invalidPayload();

    @ErrorDef(httpStatusCode = 400, code = "40004", description = "The source is missing")
    AppError missingSource();

    @ErrorDef(httpStatusCode = 400, code = "40005", description = "The action is missing")
    AppError missingAction();

    @ErrorDef(httpStatusCode = 400, code = "40006", description = "The input is invalid")
    AppError invalidInput();

    @ErrorDef(httpStatusCode = 400, code = "40007", description = "The properties is missing")
    AppError missingProperties();

    @ErrorDef(httpStatusCode = 400, code = "40008", description = "The locale is missing")
    AppError missingLocale();

    @ErrorDef(httpStatusCode = 400, code = "40009", description = "The template is not found")
    AppError templateNotFound();

    @ErrorDef(httpStatusCode = 400, code = "40010", description = "The property {0} is invalid")
    AppError invalidProperty(String property);

    @ErrorDef(httpStatusCode = 400, code = "40011", description = "The schedule_date is invalid")
    AppError invalidScheduleDate();

    @ErrorDef(httpStatusCode = 400, code = "40012", description = "The internal error")
    AppError internalError();

    @ErrorDef(httpStatusCode = 400, code = "40013", description = "The type is invalid")
    AppError invalidType();

}
