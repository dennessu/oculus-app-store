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

    @ErrorDef(httpStatusCode = 412, code = "401", message = "User Not Found.", reason = "The user_id {0} is invalid", field = "user_id")
    AppError userNotFound(String userId);

    @ErrorDef(httpStatusCode = 412, code = "402", message = "User Name Not Found.", reason = "The user name {0} is invalid")
    AppError userNameNotFound(String userPiiId);

    @ErrorDef(httpStatusCode = 412, code = "403", message = "Invalid Payment Instrument Id.",
        reason = "The payment_instrument_id {0} is invalid", field = "payment_instrument_id")
    AppError invalidPaymentInstrumentId(String piId);

    @ErrorDef(httpStatusCode = 412, code = "404", message = "Invalid Amount.",
            reason = "The amount {0} is invalid or not allowed", field = "amount")
    AppError invalidAmount(String amount);

    @ErrorDef(httpStatusCode = 412, code = "405", message = "Currency Not Found.",
            reason = "The currency {0} is invalid or not allowed", field = "currency")
    AppError currencyNotFound(String currency);

    @ErrorDef(httpStatusCode = 412, code = "406", message = "Country Not Found.",
            reason = "The country {0} is invalid or not allowed", field = "country")
    AppError countryNotFound(String country);

    @ErrorDef(httpStatusCode = 412, code = "407", message = "Invalid PI Type.",
            reason = "The payment instrument type {0} is invalid or not allowed"
            , field = "payment_instrument_type")
    AppError invalidPIType(String piType);

    @ErrorDef(httpStatusCode = 404, code = "408", message = "Payment Instrument Not Found.",
            reason = "The payment id {0} is not found", field = "payment_id")
    AppError paymentInstrumentNotFound(String paymentId);

    @ErrorDef(httpStatusCode = 412, code = "409", message = "Insufficient Balance.",
            reason = "The Stored Value Balance is insufficient")
    AppError insufficientBalance();

    @ErrorDef(httpStatusCode = 412, code = "410", message = "Billing Address Not Found.",
            reason = "The billing address id {0} is invalid")
    AppError billingAddressNotFound(String billingAddressId);

    @ErrorDef(httpStatusCode = 412, code = "411", message = "Invalid Billing Address Id",
            reason = "The billing address id {0} is invalid due to {1}")
    AppError invalidBillingAddressId(String billingAddressId, String reason);

    @ErrorDef(httpStatusCode = 412, code = "412", message = "Missing Email",
            reason = "the email info is missing")
    AppError missingEmail();

    @ErrorDef(httpStatusCode = 412, code = "413", message = "invalid BIlling Ref",
            reason = "the billing reference id is invalid: {0}")
    AppError invalidBIllingRef(String billingRef);

    @ErrorDef(httpStatusCode = 412, code = "414", message = "invalid field to update",
            reason = "{0} is not allow to be updated")
    AppError updateNotAllowed(String fieldName);

    @ErrorDef(httpStatusCode = 412, code = "415", message = "invalid user to call the api",
            reason = "{0} is not allowed")
    AppError userNotAllowed(String userId);

    @ErrorDef(httpStatusCode = 400, code = "416", message = "invalid request for provider",
            reason = "error details: {0}")
    AppError providerInvaidRequest(String error);

}
