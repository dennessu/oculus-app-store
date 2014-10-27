/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.exception.casey;

/**
 * The CaseyException class.
 */
public class CaseyException extends Exception {

    private final String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public CaseyException(String errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public CaseyException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }
}
