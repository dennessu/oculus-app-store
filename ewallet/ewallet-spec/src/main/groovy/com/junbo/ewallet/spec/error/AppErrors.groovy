package com.junbo.ewallet.spec.error

import com.junbo.common.error.AppError
import com.junbo.common.error.ErrorDef
import com.junbo.common.error.ErrorProxy

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

/**
 * appErrors for ewallet.
 */
interface AppErrors {
    static final AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors)

    @ErrorDef(httpStatusCode = 400, code = '10000', description = 'Missing Input field.', field = '{0}')
    AppError missingField(String field)

    @ErrorDef(httpStatusCode = 400, code = '10001', description = 'Unnecessary field found.', field = '{0}')
    AppError unnecessaryField(String field)

    @ErrorDef(httpStatusCode = 404, code = '10002', description = '{0} [{1}] not found.')
    AppError notFound(String entity, Long id)

    @ErrorDef(httpStatusCode = 400, code = '10003', description = 'There is not enough money in wallet [{0}].')
    AppError notEnoughMoney(Long walletId)

    @ErrorDef(httpStatusCode = 400, code = '10004', description = 'Wallet [{0}] is locked.')
    AppError locked(Long walletId)

    @ErrorDef(httpStatusCode = 400, code = '10005', description = 'Validation failed. {0}')
    AppError validation(String msg)

    @ErrorDef(httpStatusCode = 400, code = '10006', description = '{0}')
    AppError common(String msg)

    @ErrorDef(httpStatusCode = 500, code = '10007', description = 'UnCaught Exception. {0}')
    AppError unCaught(String msg)
}
