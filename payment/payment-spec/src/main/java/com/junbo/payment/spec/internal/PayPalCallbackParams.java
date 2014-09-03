/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import com.junbo.payment.common.CommonUtil;

/**
 * PayPal Callback Params.
 */
public class PayPalCallbackParams implements CallbackParams{
    public static final String provider = "PayPal";
    private String token;
    private String payerID;
    private String paymentId;
    private String billingId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPayerID() {
        return payerID;
    }

    public void setPayerID(String payerID) {
        this.payerID = payerID;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public Long getPaymentId() {
        return CommonUtil.decode(paymentId);
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBillingId() {
        return billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }
}
