/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.error;

import com.junbo.common.error.ErrorProxy;

/**
 * AppErrors.
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    /*
    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_EMAIL_ID,
            description ="The email id {0} is invalid")
    AppError invalidEmailId(String id); */
}
