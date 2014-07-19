/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.spec.error

import com.junbo.common.error.AppError
import com.junbo.common.error.ErrorDef
import com.junbo.common.error.ErrorProxy
/**
 * AppErrors.
 */
interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors)

    @ErrorDef(httpStatusCode = 400, code = "101", message = "Invalid Date Format.",
            field = "date", reason = "The date should be in yyyy-MM-ddTHH:mm:ssZ format")
    AppError invalidDateFormat()

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Offer Not Found.")
    AppError offerNotFound()
}