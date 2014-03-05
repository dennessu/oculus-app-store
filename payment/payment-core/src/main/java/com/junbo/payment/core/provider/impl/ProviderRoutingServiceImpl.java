/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.impl;

import com.junbo.payment.core.provider.PaymentProvider;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.spec.enums.PIType;

/**
 * payment provider routing implementation.
 */
public class ProviderRoutingServiceImpl implements ProviderRoutingService{

    @Override
    public PaymentProviderService getPaymentProvider(PIType piType) {
        //TODO: hard code braintree first
        return PaymentProviderRegistry.getPaymentProviderService(PaymentProvider.BrainTree);
    }

    @Override
    public void updatePaymentConfiguration() {

    }
}
