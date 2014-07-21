/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.impl;

import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.resource.PaymentTransactionResource;
import com.junbo.subscription.clientproxy.PaymentGateway;
import com.junbo.subscription.common.exception.SubscriptionExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by Administrator on 14-5-8.
 */
public class PaymentGatewayImpl implements PaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayImpl.class);

    @Autowired
    @Qualifier("paymentClient")
    private PaymentTransactionResource paymentResource;

    public PaymentTransaction chargePayment(PaymentTransaction tx){
        try {
            PaymentTransaction response = paymentResource.postPaymentCharge(tx).get();

            return response;
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Payment] component.", e);
            throw SubscriptionExceptions.INSTANCE.gatewayFailure("payment").exception();
        }
    }

}
