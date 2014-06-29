/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.error;

import com.junbo.common.error.*;

import java.math.BigDecimal;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.SHIPPING_ADDRESS_NOT_FOUND,
            description = "Shipping address with id {0} not found")
    AppError shippingAddressNotFound(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ADDRESS_NOT_FOUND,
            description = "Address with id {0} not found")
    AppError addressNotFound(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.USER_SHIPPING_ADDRESS_NOT_MATCH,
            description = "Shipping address with id {1} not belong to the user {0}")
    AppError userShippingAddressNotMatch(String userId, String addressId);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.USER_NOT_FOUND,
            description ="User with id {0} not found")
    AppError userNotFound(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.USER_STATUS_INVALID,
            description ="User with id {0} in invalid status")
    AppError userStatusInvalid(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.FIELD_MISSING_VALUE,
            description ="Field {0} has missing value")
    AppError fieldMissingValue(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.CURRENCY_NOT_FOUND,
            description ="Currency with name {0} not found")
    AppError currencyNotFound(String name);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.COUNTRY_NOT_FOUND,
            description ="Country with name {0} not found")
    AppError countryNotFound(String name);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.OFFER_NOT_FOUND,
            description ="Offer revision with name {0} not found")
    AppError offerNotFound(String name);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ORGANIZATION_NOT_FOUND,
            description ="Organization with name {0} not found")
    AppError organizationNotFound(String name);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.PAYMENT_INSTRUMENT_NOT_FOUND,
            description ="PI with id {0} not found")
    AppError piNotFound(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_BALANCE_TYPE,
            description ="Balance type {0} invalid")
    AppError invalidBalanceType(String type);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_BALANCE_TYPE,
            description ="Balance type {0} invalid, expected types: {1}")
    AppError invalidBalanceType(String type, String expectedTypes);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_BALANCE_STATUS,
            description ="Balance status {0} invalid, expected status: {1}")
    AppError invalidBalanceStatus(String status, String expectedStatus);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_BALANCE_TOTAL,
            description ="Balance total amount {0} invalid")
    AppError invalidBalanceTotal(String total);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.TAX_CALCULATION_ERROR,
            description ="Fail to calculate tax, reason: {0}")
    AppError taxCalculationError(String reason);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ADDRESS_VALIDATION_ERROR,
            description ="Fail to validate address, reason: {0}")
    AppError addressValidationError(String reason);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.BALANCE_NOT_FOUND,
            description ="Balance with id {0} not found")
    AppError balanceNotFound(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.BILLING_TRANSACTION_NOT_FOUND,
            description ="billing transaction in balance with id {0} not found")
    AppError transactionNotFound(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_PAYMENT_ID,
            description ="Payment with id {0} invalid")
    AppError invalidPaymentId(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.NOT_ASYNC_CHARGE_BALANCE,
            description ="The balance {0} is not an async charge balance")
    AppError notAsyncChargeBalance(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.PAYMENT_PROCESSING_FAILED,
            description ="The payment instrument {0} processing failed")
    AppError paymentProcessingFailed(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.PAYMENT_INSUFFICIENT_FUND,
            description ="The payment instrument {0} stored value balance insufficient")
    AppError paymentInsufficientFund(String id);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.BALANCE_REFUND_TOTAL_EXCEEDED,
            description ="The refund balance total {0} exceeded, the original total {1}, refunded total {2}")
    AppError balanceRefundTotalExceeded(BigDecimal refund, BigDecimal original, BigDecimal refunded);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.BALANCE_ITEM_REFUND_TOTAL_EXCEEDED,
            description ="The refund balance item total {0} exceeded, the original total {1}, refunded total {2}")
    AppError balanceItemRefundTotalExceeded(BigDecimal refund, BigDecimal original, BigDecimal refunded);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_ACCESS, description = "Access Denied")
    AppError accessDenied();
}
