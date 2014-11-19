/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.common.error.ErrorDetail
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * ExceptionUtil.
 */
@CompileStatic
class ExceptionUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil)
    private static final IDENTITY_COMPONENT_INPUT_VALIDATION_CODE = '131.002'

    public static void handleException(String component, Throwable throwable, ActionContextWrapper contextWrapper,
                                       Boolean rethrowException, AppError commonError) {
        LOGGER.error("Error calling the $component service", throwable)
        if (throwable instanceof AppErrorException) {
            boolean couldHandle = throwOnFieldInvalidException(throwable, contextWrapper, rethrowException)
            if (!couldHandle) {
                if (rethrowException) {
                    throw throwable
                } else {
                    contextWrapper.errors.add(((AppErrorException) throwable).error.error())
                }
            }
        } else {
            if (rethrowException) {
                throw commonError.exception()
            } else {
                contextWrapper.errors.add(commonError.error())
            }
        }
    }

    public static void handleIdentityException(Throwable throwable,
                                               ActionContextWrapper contextWrapper, Boolean rethrowException) {
        handleException('identity', throwable, contextWrapper, rethrowException, AppErrors.INSTANCE.errorCallingIdentity())
    }

    public static void handlePaymentException(Throwable throwable,
                                              ActionContextWrapper contextWrapper, Boolean rethrowException) {
        handleException('payment', throwable, contextWrapper, rethrowException, AppErrors.INSTANCE.errorCallingPayment())
    }

    public static boolean throwOnFieldInvalidException(Throwable throwable, ActionContextWrapper contextWrapper, Boolean rethrowException) {
        if (!(throwable instanceof AppErrorException)) {
            return false;
        }

        AppError appError = ((AppErrorException) throwable).error
        boolean couldHandle = false
        if ("${IDENTITY_COMPONENT_INPUT_VALIDATION_CODE}" == appError.error().code) {
            couldHandle = true
        }

        if (couldHandle) {
            LOGGER.error('name=Identity component input validation failure', throwable)

            AppError convertedError = AppCommonErrors.INSTANCE.fieldInvalid(appError.error().details.toArray(new ErrorDetail[0]))
            if (rethrowException) {
                throw convertedError.exception()
            } else {
                contextWrapper.errors.add(convertedError.error())
            }
        }

        return couldHandle
    }
}
