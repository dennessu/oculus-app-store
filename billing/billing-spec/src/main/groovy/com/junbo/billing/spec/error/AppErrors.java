/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.error;

import com.junbo.common.error.*;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.SHIPPING_ADDRESS_NOT_FOUND,
            description = "Shipping address with id {0} not found")
    AppError shippingAddressNotFound(String id);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.USER_SHIPPING_ADDRESS_NOT_MATCH,
            description = "Shipping address with id {1} not belong to the user {0}")
    AppError userShippingAddressNotMatch(String userId, String addressId);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_NOT_FOUND,
            description ="User with id {0} not found")
    AppError userNotFound(String id);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.USER_STATUS_INVALID,
            description ="User with id {0} in invalid status")
    AppError userStatusInvalid(String id);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.FIELD_MISSING_VALUE,
            description ="Field {0} has missing value")
    AppError fieldMissingValue(String field);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.CURRENCY_NOT_FOUND,
            description ="Currency with name {0} not found")
    AppError currencyNotFound(String name);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.PAYMENT_INSTRUMENT_NOT_FOUND,
            description ="PI with id {0} not found")
    AppError piNotFound(String id);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_BALANCE_TYPE,
            description ="Balance type {0} invalid")
    AppError invalidBalanceType(String type);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_BALANCE_TOTAL,
            description ="Balance total amount {0} invalid")
    AppError invalidBalanceTotal(String total);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.TAX_CALCULATION_ERROR,
            description ="Fail to calculate tax, reason: {0}")
    AppError taxCalculationError(String reason);
}
