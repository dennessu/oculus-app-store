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

    @ErrorDef(httpStatusCode = 412, code = "101", message = "User Not Found")
    AppError userNotFound()

    @ErrorDef(httpStatusCode = 412, code = "102", message = "User Personal Info Not Found")
    AppError userPersonalInfoNotFound()

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Invalid User Status")
    AppError invalidUserStatus()

    @ErrorDef(httpStatusCode = 400, code = "104", message = "Invalid Date Format",
            field = "date", reason = "The date should be in yyyy-MM-ddTHH:mm:ssZ format")
    AppError invalidDateFormat()

    @ErrorDef(httpStatusCode = 412, code = "105", message = "Offer Not Found")
    AppError offerNotFound()
}