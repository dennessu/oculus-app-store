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

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Empty User Email")
    AppError emptyUserEmail();

    @ErrorDef(httpStatusCode = 412, code = "102", message = "No Validated User Email")
    AppError noValidatedUserEmail();

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Email Schedule Not Found",
            field = "id", reason = "Email Schedule {0} is not found.")
    AppError emailScheduleNotFound(String id);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Email Template Not Found",
            field = "template", reason = "Email Template {0} is not found.")
    AppError emailTemplateNotFound(Object id);

    @ErrorDef(httpStatusCode = 409, code = "105", message = "Email Template Already Exist",
            field = "name", reason = "Email Template with name {0} already exists")
    AppError emailTemplateAlreadyExist(String name);

    @ErrorDef(httpStatusCode = 412, code = "106", message = "Invalid Placeholder Names",
            field = "{0}", reason = "The field contains invalid placeholders.")
    AppError invalidPlaceholderNames(String field);

    @ErrorDef(httpStatusCode = 412, code = "107", message = "Invalid Replacements",
            field = "cause", reason = "The email replacements don't match the template")
    AppError invalidReplacements();

    @ErrorDef(httpStatusCode = 412, code = "107", message = "Invalid Replacements",
            field = "replacements", reason = "Invalid replacement: {0}")
    AppError invalidReplacements(String replacement);
}
