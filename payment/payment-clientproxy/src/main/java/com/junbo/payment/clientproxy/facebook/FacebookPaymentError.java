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
    public FacebookPaymentError(){

    }

    public FacebookPaymentError(FacebookCCErrorDetail error){
        this.error = error;
    }
    public FacebookCCErrorDetail getError() {
        return error;
    }

    public void setError(FacebookCCErrorDetail error) {
        this.error = error;
    }

    private FacebookCCErrorDetail error;
}
