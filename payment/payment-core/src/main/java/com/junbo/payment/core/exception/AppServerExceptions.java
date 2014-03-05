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
 * Application Server Exceptions.
 */
public interface AppServerExceptions {

    AppServerExceptions INSTANCE = ErrorProxy.newProxyInstance(AppServerExceptions.class);

    @ErrorDef(httpStatusCode = 500, code = "50001", description = "The provider {0} is timeout")
    AppError providerGatewayTimeout(String provider);

    @ErrorDef(httpStatusCode = 500, code = "50002", description = "The provider {0} process with error code: {1}")
    AppError providerProcessError(String provider, String internalError);

    @ErrorDef(httpStatusCode = 500, code = "50003",
            description = "The payment instrument status {0} is invalid to process")
    AppError invalidPIStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = "50004",
            description = "The payment transaction status {0} is invalid to process")
    AppError invalidPaymentStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = "50005", description = "The required field {0} is missing while processing")
    AppError missingRequiredField(String field);

    @ErrorDef(httpStatusCode = 500, code = "50006", description = "The credit card type {0} is not recognized")
    AppError invalidCreditCardType(String ccType);

    @ErrorDef(httpStatusCode = 500, code = "50006", description = "The provider {0} request is not correct")
    AppError invalidProviderRequest(String providerName);
}
