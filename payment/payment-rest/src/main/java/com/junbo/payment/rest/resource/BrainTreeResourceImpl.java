/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;


import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.provider.braintree.BrainTreePaymentProviderServiceImpl;
import com.junbo.payment.spec.internal.BrainTreeResource;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;


/**
 * brain tree resource implimentation.
 */
public class BrainTreeResourceImpl implements BrainTreeResource {
    @Autowired
    private BrainTreePaymentProviderServiceImpl brainTreePaymentProviderService;

    @Override
    public Promise<PaymentInstrument> addPaymentInstrument(PaymentInstrument request) {
        return brainTreePaymentProviderService.add(request);

    }

    @Override
    public Promise<Response> deletePaymentInstrument(String externalToken) {
        brainTreePaymentProviderService.delete(externalToken);
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<PaymentTransaction> postAuthorization(String piToken, PaymentTransaction request) {
        return brainTreePaymentProviderService.authorize(piToken, request);
    }

    @Override
    public Promise<PaymentTransaction> postCapture(String transactionToken, PaymentTransaction request) {
        return brainTreePaymentProviderService.capture(transactionToken, request);
    }

    @Override
    public Promise<PaymentTransaction> postCharge(String piToken,PaymentTransaction request) {
        return brainTreePaymentProviderService.charge(piToken, request);
    }

    @Override
    public Promise<Response> reverse(String transactionToken) {
        brainTreePaymentProviderService.reverse(transactionToken);
        return Promise.pure(Response.status(200).build());
    }
}
