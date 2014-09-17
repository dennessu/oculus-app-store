/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

/**
 * Facebook Payment Exception.
 */
public class FacebookPaymentException extends RuntimeException {
    private final FacebookPaymentError error;
    public FacebookPaymentException(FacebookPaymentError error) {
        super(error.getError().getMessage());
        this.error = error;
    }

    public FacebookPaymentException(FacebookPaymentError error, Throwable e) {
        super(error.getError().getMessage());
        this.error = error;
    }

    public String getMessage() {
        return error.getError().getMessage();
    }
    public FacebookPaymentError getError() {
        return error;
    }
}
