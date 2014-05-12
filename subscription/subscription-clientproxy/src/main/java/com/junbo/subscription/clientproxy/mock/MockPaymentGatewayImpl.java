/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.mock;

import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.subscription.clientproxy.impl.PaymentGatewayImpl;

/**
 * Created by Administrator on 14-5-8.
 */
public class MockPaymentGatewayImpl extends PaymentGatewayImpl {
    @Override
    public PaymentTransaction chargePayment(PaymentTransaction tx){
        tx.setStatus(PaymentStatus.SETTLED.toString());
        return tx;
    }
}
