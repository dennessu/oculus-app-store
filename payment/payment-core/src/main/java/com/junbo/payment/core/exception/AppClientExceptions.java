/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.exception;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;


/**
 * Application Client Exceptions.
 */

public interface AppClientExceptions {
    AppClientExceptions INSTANCE = ErrorProxy.newProxyInstance(AppClientExceptions.class);

    @ErrorDef(httpStatusCode = 400, code = "40001", description = "The user_id {0} is invalid", field = "user_id")
    AppError invalidUserId(String userId);

    @ErrorDef(httpStatusCode = 400, code = "40002", description = "The user_id is missing", field = "user_id")
    AppError missingUserId();

    @ErrorDef(httpStatusCode = 400, code = "40003",
        description = "The payment_instrument_id {0} is invalid", field = "payment_instrument_id")
    AppError invalidPaymentInstrumentId(String piId);

    @ErrorDef(httpStatusCode = 400, code = "40004",
            description = "The payment_instrument_id is missing", field = "payment_instrument_id")
    AppError missingPaymentInstrumentId();

    @ErrorDef(httpStatusCode = 400, code = "40005",
            description = "The tracking_uuid {0} is duplicated", field = "tracking_uuid")
    AppError duplicatedTrackingUuid(String trackingUuid);

    @ErrorDef(httpStatusCode = 400, code = "40006",
            description = "The tracking_uuid is missing", field = "tracking_uuid")
    AppError missingTrackingUuid();

    @ErrorDef(httpStatusCode = 400, code = "40007",
            description = "The amount {0} is invalid or not allowed", field = "amount")
    AppError invalidAmount(String amount);

    @ErrorDef(httpStatusCode = 400, code = "40008",
            description = "The amount is missing", field = "amount")
    AppError missingAmount();

    @ErrorDef(httpStatusCode = 400, code = "40009",
            description = "The currency {0} is invalid or not allowed", field = "currency")
    AppError invalidCurrency(String amount);

    @ErrorDef(httpStatusCode = 400, code = "40010",
            description = "The currency is missing", field = "currency")
    AppError missingCurrency();

    @ErrorDef(httpStatusCode = 400, code = "40011",
            description = "The country {0} is invalid or not allowed", field = "country")
    AppError invalidCountry(String country);

    @ErrorDef(httpStatusCode = 400, code = "40012",
            description = "The country is missing", field = "country")
    AppError missingCountry();

    @ErrorDef(httpStatusCode = 400, code = "40013",
            description = "The payment instrument type {0} is invalid or not allowed"
            , field = "payment_instrument_type")
    AppError invalidPIType(String piType);

    @ErrorDef(httpStatusCode = 400, code = "40014",
            description = "The country is missing", field = "payment_instrument_type")
    AppError missingPIType();

    @ErrorDef(httpStatusCode = 400, code = "40015",
            description = "The account_name is missing", field = "account_name")
    AppError missingAccountName();

    @ErrorDef(httpStatusCode = 400, code = "40016",
            description = "The postal_code is missing", field = "postal_code")
    AppError missingPostalCode();

    @ErrorDef(httpStatusCode = 400, code = "40017",
            description = "The address_line is missing", field = "address_line")
    AppError missingAddressLine();

    @ErrorDef(httpStatusCode = 400, code = "40018",
            description = "The payment instrument type {0} is not allowed as default", field = "type")
    AppError invalidTypeForDefault(String piType);

    @ErrorDef(httpStatusCode = 400, code = "40019",
            description = "only accept format: yyyy-MM or yyyy-MM-dd", field = "expire_date")
    AppError invalidExpireDateFormat(String date);

    @ErrorDef(httpStatusCode = 400, code = "40020",
            description = "the payment id {0} is invalid", field = "payment_id")
    AppError invalidPaymentId(String paymentId);

    @ErrorDef(httpStatusCode = 404, code = "40401",
            description = "the resource {0} is not found", field = "resource")
    AppError resourceNotFound(String resource);

    @ErrorDef(httpStatusCode = 400, code = "40021",
            description = "the expire date of credit card is missing", field = "expire_date")
    AppError missingExpireDate();
}
