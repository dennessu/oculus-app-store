/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

/**
 * payment call back property.
 */
public class PaymentProperties {
    private String externalAccessToken;
    private String externalPayerId;

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
}
