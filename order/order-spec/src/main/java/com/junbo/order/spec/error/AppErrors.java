/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_NOT_FOUND, description = "Order not found")
    AppError orderNotFound();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_ITEM_NOT_FOUND, description = "Order item not found")
    AppError orderItemNotFound();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_NOT_FOUND, description = "User not found")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.USER_STATUS_INVALID, description = "User status invalid")
    AppError userStatusInvalid();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.PAYMENT_STATUS_INVALID, description = "Payment status invalid")
    AppError paymentStatusInvalid();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD, description = "{1}", field = "{0}")
    AppError fieldInvalid(String field, String message);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD, description = "Field value invalid", field = "{0}")
    AppError fieldInvalid(String field);
}
