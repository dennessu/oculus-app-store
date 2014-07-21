/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.common.exception;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;


/**
 * Application Client Exceptions.
 */

public interface AppClientExceptions {
    AppClientExceptions INSTANCE = ErrorProxy.newProxyInstance(AppClientExceptions.class);

    @ErrorDef(httpStatusCode = 400, code = "40001", message = "The user_id {0} is invalid", field = "user_id")
    AppError invalidUserId(String userId);

    @ErrorDef(httpStatusCode = 400, code = "40002", message = "The user_id is missing", field = "user_id")
    AppError missingUserId();

    @ErrorDef(httpStatusCode = 400, code = "40003",
        message = "The payment_instrument_id {0} is invalid", field = "payment_instrument_id")
    AppError invalidPaymentInstrumentId(String piId);

    @ErrorDef(httpStatusCode = 400, code = "40004",
            message = "The payment_instrument_id is missing", field = "payment_instrument_id")
    AppError missingPaymentInstrumentId();

    @ErrorDef(httpStatusCode = 400, code = "40005",
            message = "The tracking_uuid {0} is duplicated", field = "tracking_uuid")
    AppError duplicatedTrackingUuid(String trackingUuid);

    @ErrorDef(httpStatusCode = 400, code = "40006",
            message = "The tracking_uuid is missing", field = "tracking_uuid")
    AppError missingTrackingUuid();

    @ErrorDef(httpStatusCode = 400, code = "40007",
            message = "The amount {0} is invalid or not allowed", field = "amount")
    AppError invalidAmount(String amount);

    @ErrorDef(httpStatusCode = 400, code = "40008",
            message = "The amount is missing", field = "amount")
    AppError missingAmount();

    @ErrorDef(httpStatusCode = 400, code = "40009",
            message = "The currency {0} is invalid or not allowed", field = "currency")
    AppError invalidCurrency(String amount);

    @ErrorDef(httpStatusCode = 400, code = "40010",
            message = "The currency is missing", field = "currency")
    AppError missingCurrency();

    @ErrorDef(httpStatusCode = 400, code = "40011",
            message = "The country {0} is invalid or not allowed", field = "country")
    AppError invalidCountry(String country);

    @ErrorDef(httpStatusCode = 400, code = "40012",
            message = "The country is missing", field = "country")
    AppError missingCountry();

    @ErrorDef(httpStatusCode = 400, code = "40013",
            message = "The payment instrument type {0} is invalid or not allowed"
            , field = "payment_instrument_type")
    AppError invalidPIType(String piType);

    @ErrorDef(httpStatusCode = 400, code = "40014",
            message = "The country is missing", field = "payment_instrument_type")
    AppError missingPIType();

    @ErrorDef(httpStatusCode = 400, code = "40015",
            message = "The account_name is missing", field = "account_name")
    AppError missingAccountName();

    @ErrorDef(httpStatusCode = 400, code = "40016",
            message = "The postal_code is missing", field = "postal_code")
    AppError missingPostalCode();

    @ErrorDef(httpStatusCode = 400, code = "40017",
            message = "The address_line is missing", field = "address_line")
    AppError missingAddressLine();

    @ErrorDef(httpStatusCode = 400, code = "40018",
            message = "The payment instrument type {0} is not allowed as default", field = "type")
    AppError invalidTypeForDefault(String piType);

    @ErrorDef(httpStatusCode = 400, code = "40019",
            message = "only accept format: yyyy-MM or yyyy-MM-dd", field = "expire_date")
    AppError invalidExpireDateFormat(String date);

    @ErrorDef(httpStatusCode = 400, code = "40020",
            message = "the payment id {0} is invalid", field = "payment_id")
    AppError invalidPaymentId(String paymentId);

    @ErrorDef(httpStatusCode = 404, code = "40401",
            message = "the resource {0} is not found", field = "resource")
    AppError resourceNotFound(String resource);

    @ErrorDef(httpStatusCode = 400, code = "40021",
            message = "the expire date of credit card is missing", field = "expire_date")
    AppError missingExpireDate();

    @ErrorDef(httpStatusCode = 400, code = "40022",
            message = "the field {0} is not needed")
    AppError fieldNotNeeded(String fieldName);

    @ErrorDef(httpStatusCode = 400, code = "40023",
            message = "the billing ref id is missing for the payment")
    AppError missingBillingRefId();

    @ErrorDef(httpStatusCode = 400, code = "40024",
            message = "The account_num is missing", field = "account_number")
    AppError missingAccountNum();

    @ErrorDef(httpStatusCode = 400, code = "40025",
            message = "The revision number is missing", field = "revision")
    AppError missingRevision();

    @ErrorDef(httpStatusCode = 400, code = "40025",
            message = "The revision number is invalid", field = "revision")
    AppError invalidRevision();

    @ErrorDef(httpStatusCode = 400, code = "40026",
            message = "The property field {0} is invalid", field = "property")
    AppError invalidPropertyField(String fieldName);

    @ErrorDef(httpStatusCode = 400, code = "40027",
            message = "The wallet type is missing", field = "wallet_type")
    AppError missingWalletType();

    @ErrorDef(httpStatusCode = 400, code = "40028",
            message = "The Stored Value Balance is insufficient")
    AppError insufficientBalance();

    @ErrorDef(httpStatusCode = 400, code = "40029",
            message = "The billing address id {0} is invalid")
    AppError invalidBillingAddressId(String billingAddressId);

    @ErrorDef(httpStatusCode = 400, code = "40029",
            message = "The billing address id {0} is invalid due to {1}")
    AppError invalidBillingAddressId(String billingAddressId, String reason);
}
