/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorDetail;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.*;

import java.math.BigDecimal;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Address Not Found",
            field = "{0}", reason = "Address with ID {1} is not found")
    AppError addressNotFound(String field, UserPersonalInfoId id);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "User Not Found",
            field = "{0}", reason = "User with ID {1} is not found")
    AppError userNotFound(String field, UserId id);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "User Status Invalid",
            field = "{0}", reason = "User {1} with status {2} is invalid for the operation")
    AppError userStatusInvalid(String field, UserId id, String status);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Organization Not Found",
            field = "{0}", reason = "Organization with ID {1} is not found")
    AppError organizationNotFound(String field, OrganizationId id);

    @ErrorDef(httpStatusCode = 412, code = "105", message = "Currency Not Found",
            field = "currency", reason = "Current with ID {1} is not found")
    AppError currencyNotFound(String name);

    @ErrorDef(httpStatusCode = 412, code = "106", message = "Country Not Found",
            field = "country", reason = "Country with ID {1} is not found")
    AppError countryNotFound(String name);

    @ErrorDef(httpStatusCode = 412, code = "107", message = "Offer Not Found",
            field = "{0}", reason = "Offer with ID {1} is not found")
    AppError offerNotFound(String field, String offerId);

    @ErrorDef(httpStatusCode = 412, code = "108", message = "Payment Instrument Not Found",
            field = "{0}", reason = "Payment Instrument with ID {1} is not found")
    AppError piNotFound(String field, PaymentInstrumentId paymentInstrumentId);

    @ErrorDef(httpStatusCode = 412, code = "109", message = "Invalid Balance Type",
            field = "type", reason = "Balance type {0} is invalid")
    AppError invalidBalanceType(String balanceType);

    @ErrorDef(httpStatusCode = 412, code = "110", message = "Invalid Balance Status",
            field = "status", reason = "Balance status {0} is invalid, expected status: {1}")
    AppError invalidBalanceStatus(String status, String expectedStatus);

    @ErrorDef(httpStatusCode = 412, code = "111", message = "Invalid Balance Total Amount",
            field = "totalAmount", reason = "Balance total amount {0} is invalid")
    AppError invalidBalanceTotal(String total);

    @ErrorDef(httpStatusCode = 412, code = "112", message = "Failed to Calculate Tax",
            field = "cause", reason = "Fail to calculate tax, reason: {0}")
    AppError taxCalculationError(String reason);

    @ErrorDef(httpStatusCode = 412, code = "113", message = "Address Validation Error")
    AppError addressValidationError();

    @ErrorDef(httpStatusCode = 412, code = "113", message = "Address Validation Error")
    AppError addressValidationError(ErrorDetail[] errorDetails);

    @ErrorDef(httpStatusCode = 412, code = "114", message = "Balance Not Found",
            field = "{0}", reason = "Balance with id {1} is not found")
    AppError balanceNotFound(String field, BalanceId balanceId);

    @ErrorDef(httpStatusCode = 412, code = "115", message = "Transaction Not Found",
            field = "{0}", reason = "Balance with id {0} doesn't contain any billing transaction")
    AppError transactionNotFound(BalanceId balanceId);

    @ErrorDef(httpStatusCode = 412, code = "116", message = "Payment Not Found",
            field = "{0}", reason = "Payment with id {0} is not found")
    AppError paymentNotFound(String field, PaymentId id);

    @ErrorDef(httpStatusCode = 412, code = "117", message = "Balance Not Async",
            field = "cause", reason = "The balance {0} is not an async charge balance")
    AppError notAsyncChargeBalance(BalanceId id);

    @ErrorDef(httpStatusCode = 412, code = "118", message = "Payment Processing Failed",
            field = "cause", reason = "The payment instrument {0} processing failed")
    AppError paymentProcessingFailed(PaymentInstrumentId id);

    @ErrorDef(httpStatusCode = 412, code = "119", message = "Payment Insufficient Fund",
            field = "cause", reason = "The payment instrument {0} stored value balance insufficient")
    AppError paymentInsufficientFund(PaymentInstrumentId id);

    @ErrorDef(httpStatusCode = 412, code = "120", message = "Balance Refund Total Exceeded",
            field = "cause", reason = "The refund balance {0} total {1} exceeded, the original total {2}, refunded total {3}")
    AppError balanceRefundTotalExceeded(BalanceId balanceId, BigDecimal refund, BigDecimal original, BigDecimal refunded);

    @ErrorDef(httpStatusCode = 412, code = "121", message = "Balance Item Refund Total Exceeded",
            field = "cause", reason = "The refund balance {0} item {1} total {2} exceeded, the original total {3}, refunded total {4}")
    AppError balanceItemRefundTotalExceeded(BalanceId balanceId, String balanceItemId, BigDecimal refund, BigDecimal original, BigDecimal refunded);
}
