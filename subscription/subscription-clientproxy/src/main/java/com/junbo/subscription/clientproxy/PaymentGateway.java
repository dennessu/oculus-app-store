/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy;

import com.junbo.payment.spec.model.PaymentTransaction;
/**
 * Created by Administrator on 14-5-8.
 */
public interface PaymentGateway {
    PaymentTransaction chargePayment(PaymentTransaction tx);
}
