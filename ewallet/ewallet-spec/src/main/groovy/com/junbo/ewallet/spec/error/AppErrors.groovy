package com.junbo.ewallet.spec.error

import com.junbo.common.error.AppError
import com.junbo.common.error.ErrorDef
import com.junbo.common.error.ErrorProxy
import groovy.transform.CompileStatic

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

/**
 * appErrors for ewallet.
 */
@CompileStatic
interface AppErrors {
    static final AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors)

    @ErrorDef(httpStatusCode = 412, code = '101', message = ErrorMessages.INSUFFICIENT_FUND)
    AppError insufficientFund()

    @ErrorDef(httpStatusCode = 412, code = '102', message = ErrorMessages.WALLET_LOCKED)
    AppError locked()
}
