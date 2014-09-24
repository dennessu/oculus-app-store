/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

/**
 * Facebook Payment Error.
 */
public class FacebookPaymentError {
    public FacebookPaymentError(Error error){
        this.error = error;
    }
    public Error getError() {
        return error;
    }

    private final Error error;

    static class Error {
        public Error(String message, String type, int code){
            this.message = message;
            this.type = type;
            this.code = code;
        }
        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }

        public int getCode() {
            return code;
        }

        private final String message;
        private final String type;
        private final int code;
    }
}
