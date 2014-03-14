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

    @ErrorDef(httpStatusCode = 400, code = '10000', description = 'Missing Input field.', field = '{0}')
    AppError missingField(String field)

    @ErrorDef(httpStatusCode = 400, code = '10001', description = 'Unnecessary field found.', field = '{0}')
    AppError unnecessaryField(String field)

    @ErrorDef(httpStatusCode = 400, code = '10002', description = 'Field is not correct. {1}', field = '{0}')
    AppError fieldNotCorrect(String fieldName, String msg)

    @ErrorDef(httpStatusCode = 403, code = '10003',
            description = "{0} doesn't match. actually: {1}, expected: {2}.",
            field = '{0}')
    AppError fieldNotMatch(String fieldName, Object actually, Object expected)

    @ErrorDef(httpStatusCode = 404, code = '10004', description = '{0} [{1}] not found.')
    AppError notFound(String entity, Long id)

    @ErrorDef(httpStatusCode = 400, code = '10005', description = 'There is not enough money in wallet [{0}].')
    AppError notEnoughMoney(Long walletId)

    @ErrorDef(httpStatusCode = 400, code = '10006', description = 'Wallet [{0}] is locked.')
    AppError locked(Long walletId)

    @ErrorDef(httpStatusCode = 400, code = '10007', description = 'Validation failed. {0}')
    AppError validation(String msg)

    @ErrorDef(httpStatusCode = 400, code = '10008', description = '{0}')
    AppError common(String msg)

    @ErrorDef(httpStatusCode = 500, code = '10009', description = 'UnCaught Exception. {0}')
    AppError unCaught(String msg)
}
