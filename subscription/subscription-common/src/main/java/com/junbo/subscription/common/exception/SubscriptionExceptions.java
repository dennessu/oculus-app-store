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

    @ErrorDef(httpStatusCode = 400, code = "40001", description = "The trackingUuid is missing",
            field = "tracking_uuid")
    AppError missingTrackingUuid();

}
