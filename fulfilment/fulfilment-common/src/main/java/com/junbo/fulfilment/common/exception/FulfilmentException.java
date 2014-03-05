/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.exception;

/**
 * FulfilmentException.
 */
public class FulfilmentException extends RuntimeException {
    public FulfilmentException(String message) {
        super(message);
    }

    public FulfilmentException(String message, Exception ex) {
        super(message, ex);
    }
}
