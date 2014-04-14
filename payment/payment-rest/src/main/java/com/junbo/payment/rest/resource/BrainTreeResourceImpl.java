/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;


import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.provider.braintree.BrainTreePaymentProviderServiceImpl;
import com.junbo.payment.spec.internal.BrainTreeResource;
import com.junbo.payment.spec.model.CreditCardRequest;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.util.List;


/**
 * brain tree resource implementation.
 */
public class BrainTreeResourceImpl implements BrainTreeResource {
    @Autowired
    private BrainTreePaymentProviderServiceImpl brainTreePaymentProviderService;

    @Override
    public Promise<PaymentInstrument> addPaymentInstrument(PaymentInstrument request) {
        return brainTreePaymentProviderService.add(request);
    }

    @Override
    public Promise<Response> deletePaymentInstrument(final String externalToken) {
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExternalToken(externalToken);
                    }
                });
            }
        };
        brainTreePaymentProviderService.delete(pi);
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<PaymentTransaction> postAuthorization(final String piToken, PaymentTransaction request) {
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExternalToken(piToken);
                    }
                });
            }
        };
        return brainTreePaymentProviderService.authorize(pi, request);
    }

    @Override
    public Promise<PaymentTransaction> postCapture(String transactionToken, PaymentTransaction request) {
        return brainTreePaymentProviderService.capture(transactionToken, request);
    }

    @Override
    public Promise<PaymentTransaction> postCharge(final String piToken,PaymentTransaction request) {
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExternalToken(piToken);
                    }
                });
            }
        };
        return brainTreePaymentProviderService.charge(pi, request);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionToken,PaymentTransaction request) {
        return brainTreePaymentProviderService.reverse(transactionToken, request);
    }

    @Override
    public Promise<PaymentTransaction> getById(String transactionToken) {
        return brainTreePaymentProviderService.getByTransactionToken(transactionToken);
    }

    @Override
    public Promise<Results<PaymentTransaction>> getByOrderId(String orderId) {
        List<PaymentTransaction> transactions = brainTreePaymentProviderService.getByBillingRefId(orderId);
        Results<PaymentTransaction> result = new Results<PaymentTransaction>();
        result.setItems(transactions);
        return Promise.pure(result);
    }
}
