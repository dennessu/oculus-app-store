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

    @ErrorDef(httpStatusCode = 504, code = "501", message = "Provider Gateway Timeout", reason = "The provider {0} is timeout")
    AppError providerGatewayTimeout(String provider);

    @ErrorDef(httpStatusCode = 500, code = "502", message = "Provider Process Error", reason = "The provider {0} process with error code: {1}")
    AppError providerProcessError(String provider, String internalError);

    @ErrorDef(httpStatusCode = 500, code = "503", message = "Invalid PI",
            reason = "The payment instrument is invalid")
    AppError invalidPI();

    @ErrorDef(httpStatusCode = 500, code = "504", message = "Invalid Payment Status",
            reason = "The payment transaction status {0} is invalid to process")
    AppError invalidPaymentStatus(String status);

    @ErrorDef(httpStatusCode = 500, code = "505", message = "MerchantRef Not Available", reason = "merchantRef not available for currency:{0}")
    AppError merchantRefNotAvailable(String currency);

    @ErrorDef(httpStatusCode = 500, code = "506", message = "Provider Not Found", reason = "No provider found for criteria: {0}")
    AppError providerNotFound(String criteria);

    @ErrorDef(httpStatusCode = 500, code = "507", message = "Service Not Implemented", reason = "Service {0} is not Implemented")
    AppError serviceNotImplemented(String serviceName);

    @ErrorDef(httpStatusCode = 500, code = "509", message = "No External Token Found For Payment", reason = "payment {0} has no external token")
    AppError noExternalTokenFoundForPayment(String paymentId);

    @ErrorDef(httpStatusCode = 500, code = "510", message = "Invalid Id To Encode", reason = "error happens when encode the id")
    AppError invalidIdToEncode(String paymentId);

    @ErrorDef(httpStatusCode = 500, code = "511", message = "Error Calculate HMCA", reason = "error happens when calculation the HMCA")
    AppError errorCalculateHMCA();

    @ErrorDef(httpStatusCode = 401, code = "512", message = "UnAuthorized", reason = "{0} is un-authorized for calling API")
    AppError unAuthorized(String authCode);

    @ErrorDef(httpStatusCode = 401, code = "513", message = "Invalid Platform", reason = "Platform {platform} is invalid")
    AppError invalidPlatform(String platform);

    @ErrorDef(httpStatusCode = 500, code = "514", message = "Invalid Batch", reason = "invalid batch file: {0}")
    AppError invalidBatchFile(String filePath);

    @ErrorDef(httpStatusCode = 500, code = "515", message = "Error Parse Batch", reason = "error parse batch file: {0}")
    AppError errorParseBatchFile(String filePath);

    @ErrorDef(httpStatusCode = 500, code = "516", message = "Error encode parameter", reason = "error encode: {0}")
    AppError errorEncode(String para);

    @ErrorDef(httpStatusCode = 500, code = "517", message = "Error decode parameter", reason = "error decode: {0}")
    AppError errorDecode(String para);

    @ErrorDef(httpStatusCode = 500, code = "518", message = "Error download file", reason = "error download file: {0}")
    AppError errorDownloadFile(String filePath);
}
