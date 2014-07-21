/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common.exception;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;


/**
 * Application Client Exceptions.
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "101", message = "Invalid Token")
    AppError invalidToken();

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Token Order Not Found",
            field = "tokenOrderId", reason = "Token order with ID {0} is not found")
    AppError tokenOrderNotFound(String id);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Offer Not Found",
            field = "offerId", reason = "Offer with ID {0} is not found")
    AppError offerNotFound(String id);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Invalid Token Status",
            field = "status", reason = "The token status {0} is not invalid")
    AppError invalidTokenStatus(String tokenStatus);

    @ErrorDef(httpStatusCode = 412, code = "105", message = "Token Expired")
    AppError tokenExpired();

    @ErrorDef(httpStatusCode = 412, code = "106", message = "Token Usage Limit Exceeded")
    AppError tokenUsageLimitExceeded();

    @ErrorDef(httpStatusCode = 412, code = "107", message = "Invalid Product",
            field = "product", reason = "The product {0} is invalid")
    AppError invalidProduct(String product);
}
