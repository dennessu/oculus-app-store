/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Created by Shenhua on 5/13/2014.
 */
public interface FilterErrors {
    FilterErrors INSTANCE = ErrorProxy.newProxyInstance(FilterErrors.class);

    @ErrorDef(httpStatusCode = 403, code = "2000001", description = "Field {0} not writable.", field = "{0}")
    AppError fieldNotWritable(String field);
}
