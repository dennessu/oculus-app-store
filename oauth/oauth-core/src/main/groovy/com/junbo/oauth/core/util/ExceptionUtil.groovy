/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
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

    public static void handleException(String component, Throwable throwable, ActionContextWrapper contextWrapper,
                                       Boolean rethrowException, AppError commonError) {
        LOGGER.error("Error calling the $component service", throwable)
        if (throwable instanceof AppErrorException) {
            if (rethrowException) {
                throw throwable
            } else {
                contextWrapper.errors.add(((AppErrorException) throwable).error.error())
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
}
