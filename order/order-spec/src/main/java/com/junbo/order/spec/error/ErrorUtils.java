/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import com.junbo.common.error.ErrorDetail;

/**
 * Created by fzhang on 4/30/2014.
 */
public class ErrorUtils {

    public static AppError[] toAppErrors(Throwable throwable) {
        AppError error = toAppError(throwable);
        if (error == null) {
            return new AppError[0];
        }
        return new AppError[]{error};
    }

    public static AppError toAppError(Throwable throwable) {
        if (throwable instanceof AppErrorException) {
            return ((AppErrorException) throwable).getError();
        }
        return null;
    }

    public static ErrorDetail[] getErrorDetails(AppError appError) {
        if (appError.error() != null && appError.error().getDetails() != null) {
            return appError.error().getDetails().toArray(new ErrorDetail[0]);
        }
        return null;
    }

    public static AppError processBillingError(Throwable throwable) {
        AppError appError = toAppError(throwable);
        if (appError == null) {
            return AppErrors.INSTANCE.billingConnectionError(throwable.getMessage());
        } else {
            if (appError.getHttpStatusCode() / 100 == 5) {
                return AppErrors.INSTANCE.billingConnectionError(throwable.getMessage());
            }
            return appError;
        }
    }
}
