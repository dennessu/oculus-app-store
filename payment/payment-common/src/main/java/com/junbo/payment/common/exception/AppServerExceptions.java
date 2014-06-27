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
 * Application Server Exceptions.
 */
public interface AppServerExceptions {

    AppServerExceptions INSTANCE = ErrorProxy.newProxyInstance(AppServerExceptions.class);

    @ErrorDef(httpStatusCode = 504, code = "50001", description = "The provider {0} is timeout")
    AppError providerGatewayTimeout(String provider);

    @ErrorDef(httpStatusCode = 500, code = "50002", description = "The provider {0} process with error code: {1}")
    AppError providerProcessError(String provider, String internalError);

    @ErrorDef(httpStatusCode = 500, code = "50003",
            description = "The payment instrument is invalid")
    AppError invalidPI();

    @ErrorDef(httpStatusCode = 500, code = "50004",
            description = "The payment transaction status {0} is invalid to process")
    AppError invalidPaymentStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = "50005", description = "The required field {0} is missing while processing")
    AppError missingRequiredField(String field);

    @ErrorDef(httpStatusCode = 500, code = "50006", description = "The credit card type {0} is not recognized")
    AppError invalidCreditCardType(String ccType);

    @ErrorDef(httpStatusCode = 500, code = "50006", description = "The provider {0} request is not correct")
    AppError invalidProviderRequest(String providerName);

    @ErrorDef(httpStatusCode = 500, code = "50007", description = "merchantRef not available for currency:{0}")
    AppError merchantRefNotAvailable(String currency);

    @ErrorDef(httpStatusCode = 500, code = "50008", description = "No provider found for criteria: {0}")
    AppError providerNotFound(String criteria);

    @ErrorDef(httpStatusCode = 500, code = "50009", description = "Service {0} is not Implemented")
    AppError serviceNotImplemented(String serviceName);

    @ErrorDef(httpStatusCode = 500, code = "50010", description = "payment {0} has no external token")
    AppError noExternalTokenFoundForPayment(String paymentId);

    @ErrorDef(httpStatusCode = 500, code = "50011", description = "error happens when encode the id")
    AppError invalidIdToEncode(String paymentId);

    @ErrorDef(httpStatusCode = 500, code = "50012", description = "error happens when calculation the HMCA")
    AppError errorCalculateHMCA();

    @ErrorDef(httpStatusCode = 401, code = "50014", description = "{0} is un-authorized for calling API")
    AppError unAuthorized(String authCode);

    @ErrorDef(httpStatusCode = 401, code = "50015", description = "Platform {platform} is invalid")
    AppError invalidPlatform(String platform);
}
