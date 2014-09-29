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

    public CaseyException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaseyException(Throwable cause) {
        super(cause);
    }
}
