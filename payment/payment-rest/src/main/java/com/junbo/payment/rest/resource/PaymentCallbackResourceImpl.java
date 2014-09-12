/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.spec.resource.PaymentCallbackResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;

/**
 * payment callback resource implementation.
 */
public class PaymentCallbackResourceImpl implements PaymentCallbackResource{
    @Autowired
    private PaymentCallbackService paymentCallbackService;

    @Override
    public Promise<Response> postPaymentProperties(String request) {
        paymentCallbackService.addPaymentProperties(request);
        return Promise.pure(null);
    }
}
