/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.exception;

import com.junbo.test.common.libs.LogHelper;

/**
 * Created by Yunlong on 3/21/14.
 */
public class TestException extends RuntimeException {
    private final LogHelper logHelper = new LogHelper(TestException.class);

    public TestException(String message) {
        this(null, message);
    }

    public TestException(Throwable throwable, String message) {
        super(message, throwable);
        logHelper.logError(message);
    }

}
