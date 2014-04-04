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
        if(piType.equals(PIType.CREDITCARD)){
            return PaymentProviderRegistry.getPaymentProviderService(PaymentProvider.BrainTree);
        }else if(piType.equals(PIType.WALLET)){
            return PaymentProviderRegistry.getPaymentProviderService(PaymentProvider.Wallet);
        }
        else{
            return null;
        }
    }

    @Override
    public void updatePaymentConfiguration() {

    }
}
