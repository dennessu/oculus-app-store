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

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_NOT_FOUND,
            description ='User not found')
    AppError invalidUser()

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_PII_NOT_FOUND,
            description ='User information not found')
    AppError invalidUserPii()

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_USER_STATUS,
            description ='User status is invalid')
    AppError invalidUserStatus()

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.UNNECESSARY_FILED,
            description ='Field {0} is unnecessary')
    AppError unnecessaryField(String field)

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_FIELD_VALUE,
            description ='Field {0} is invalid')
    AppError invalidField(String field)

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.MISSING_FIELD_VALUE,
            description ='Field {0} is missing')
    AppError missingField(String field)

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.QUERY_PARAMETER_IS_NULL,
            description ='The query parameter is null')
    AppError invalidQueryParameter()

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_DATE_FORMAT,
            description ='The date should be in yyyy-MM-ddTHH:mm:ssZ format')
    AppError invalidDateFormat()

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.GET_OFFER_FAILED,
            description ='Failed to get offer info')
    AppError getOfferFailed()

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.GET_USER_FAILED,
            description ='Failed to get user')
    AppError getUserFailed()
}