/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.error;

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

    @ErrorDef(httpStatusCode = 412, code = "101", message = "User Not Found",
            field = "{0}", reason = "User with ID {1} is not found")
    AppError userNotFound(String field, UserId id);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "User Status Invalid",
            field = "{0}", reason = "User {1} with status {2} is invalid for the operation")
    AppError userStatusInvalid(String field, UserId id, String status);

    @ErrorDef(httpStatusCode = 409, code = "103", message = "Cart Already Exists",
            field = "cause", reason = "Cart with name already exist. user: {0}, cartName: {1}")
    AppError cartAlreadyExists(UserId userId, String cartName);
}
