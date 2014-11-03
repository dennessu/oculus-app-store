/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.webflow;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * WebFlowErrors.
 */
public interface WebFlowErrors {
    WebFlowErrors INSTANCE = ErrorProxy.newProxyInstance(WebFlowErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "001", message = "Input Error", field = "cid", reason = "Conversation Verify Failed")
    AppError conversationVerifyFailed();

    @ErrorDef(httpStatusCode = 400, code = "002", message = "Input Error", field = "cid", reason = "Conversation Ip Violation")
    AppError ipViolation();
}
