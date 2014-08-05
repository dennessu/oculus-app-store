/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * AppErrors.
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "101", message = "User Not Found.")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 400, code = "102", message = "Item Not Found for Package Name {0}.")
    AppError itemNotFoundForPackageName(String packageName);
}
