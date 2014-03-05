/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.impl;

import com.junbo.payment.core.provider.PaymentProvider;
import com.junbo.payment.core.provider.PaymentProviderService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * payment provider registry.
 */
public class PaymentProviderRegistry {
    private static ConcurrentHashMap<PaymentProvider, PaymentProviderService> registry;

    public void setRegistry(ConcurrentHashMap<PaymentProvider, PaymentProviderService> registry) {
        this.registry = registry;
    }

    public static PaymentProviderService getPaymentProviderService(PaymentProvider paymentProvider){
        return registry.get(paymentProvider);
    }
}
