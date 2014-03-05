/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.braintree;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.spec.internal.BrainTreeResource;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

/**
 * brain tree rest implementation.
 */
public class BrainTreeRestProviderServiceImpl implements PaymentProviderService {
    private static final String PROVIDER_NAME = "BrainTree";
    private BrainTreeResource brainTreeResource;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        return  brainTreeResource.addPaymentInstrument(request);
    }

    @Override
    public Promise<Void> delete(String token) {
        brainTreeResource.deletePaymentInstrument(token);
        return null;
    }

    @Override
    public Promise<PaymentTransaction> authorize(String piToken, PaymentTransaction paymentRequest) {
        return brainTreeResource.postAuthorization(piToken, paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        return brainTreeResource.postCapture(transactionId, paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> charge(String piToken, PaymentTransaction paymentRequest) {
        return brainTreeResource.postCharge(piToken, paymentRequest);
    }

    @Override
    public Promise<Void> reverse(String transactionId) {
        brainTreeResource.reverse(transactionId);
        return null;
    }

    @Override
    public void refund(String transactionId, PaymentTransaction request) {

    }

    public void setBrainTreeResource(BrainTreeResource brainTreeResource) {
        this.brainTreeResource = brainTreeResource;
    }
}
