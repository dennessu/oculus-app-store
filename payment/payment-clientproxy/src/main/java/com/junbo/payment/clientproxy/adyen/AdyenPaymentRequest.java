/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.adyen;

import com.adyen.services.payment.PaymentRequest;

/**
 * Created by Administrator on 14-5-27.
 */
public class AdyenPaymentRequest extends PaymentRequest {
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
