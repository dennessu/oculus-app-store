/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.common.exception;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * exceptions.
 */
public interface SubscriptionExceptions {

    SubscriptionExceptions INSTANCE = ErrorProxy.newProxyInstance(SubscriptionExceptions.class);

    @ErrorDef(httpStatusCode = 400, code = "40001", message = "The trackingUuid is missing: {0}",
            field = "tracking_uuid")
    AppError missingTrackingUuid();

    @ErrorDef(httpStatusCode = 400, code = "40002", message = "The offer id is missing.")
    AppError missingOfferId();


    @ErrorDef(httpStatusCode = 500, code = "50001", message = "this is not a subscrption offer: {0}",
            field = "offer_id")
    AppError subscriptionTypeError();

    @ErrorDef(httpStatusCode = 500, code = "50002", message = "Exception occurred during calling [{0}] component.")
    AppError gatewayFailure(String gateway);

}
