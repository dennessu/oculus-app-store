/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

/**
 * payment call back property.
 */
public class PaymentCallbackParams {
    //paypal
    private String externalAccessToken;
    private String externalPayerId;
    //adyen
    private String authResult;
    private String pspReference;


    public String getExternalPayerId() {
        return externalPayerId;
    }

    public void setExternalPayerId(String externalPayerId) {
        this.externalPayerId = externalPayerId;
    }

    public String getExternalAccessToken() {
        return externalAccessToken;
    }

    public void setExternalAccessToken(String externalAccessToken) {
        this.externalAccessToken = externalAccessToken;
    }

    public String getPspReference() {
        return pspReference;
    }

    public void setPspReference(String pspReference) {
        this.pspReference = pspReference;
    }

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        this.authResult = authResult;
    }
}
