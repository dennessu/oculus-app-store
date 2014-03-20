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

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_NULL_EMPTY_INPUT_PARAM,
            description ="Invalid null/empty input parameter")
    AppError invalidNullEmptyInputParam();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_OBJECT_TYPE,
            description = "Object type doesn't map. actually: {0}, expected: {1}.")
    AppError invalidObjectType(Class actually, Class expected);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.MISSING_PARAMETER_FIELD,
            description = "Missing Input field. field: {0}")
    AppError missingParameterField(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.UNNECESSARY_PARAMETER_FIELD,
            description = "Unnecessary field found. field: {0}")
    AppError unnecessaryParameterField(String field);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_NOT_FOUND,
            description = "Order not found")
    AppError orderNotFound();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_ITEM_NOT_FOUND,
            description = "Order item not found")
    AppError orderItemNotFound();

    @ErrorDef(httpStatusCode = 404, code = UserErrorCode.USER_NOT_FOUND,
            description = "User not found")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 403, code = UserErrorCode.USER_STATUS_INVALID,
            description = "User status invalid")
    AppError userStatusInvalid();

    @ErrorDef(httpStatusCode = 403, code = PaymentErrorCode.PAYMENT_INSTRUMENT_STATUS_INVALID,
            description = "Payment instrument {0} status invalid.")
    AppError paymentInstrumentStatusInvalid(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 404, code = PaymentErrorCode.PAYMENT_INSTRUMENT_NOT_FOUND,
            description = "Payment instrument {0} not found.")
    AppError paymentInstrumentNotFound(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 403, code = PaymentErrorCode.PAYMENT_CONNECTION_ERROR,
            description = "Payment connection error")
    AppError paymentConnectionError();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD,
            description = "{1}", field = "{0}")
    AppError fieldInvalid(String field, String message);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD,
            description = "Field value invalid", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ENUM_CONVERSION_ERROR,
            description = "Enum value {0} not exists in type {1}")
    AppError enumConversionError(String enumValue, String enumType);
}
