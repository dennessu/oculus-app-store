/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.mock;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppMockErrors {
    AppMockErrors INSTANCE = ErrorProxy.newProxyInstance(AppMockErrors.class);
    @ErrorDef(httpStatusCode = 404, code = "101", message = "Order Not Found.")
    AppError error404();

    @ErrorDef(httpStatusCode = 501, code = "102", message = "Order Action Not Supported.", field = "action", reason = "Order action is not supported.")
    AppError error501();
}
