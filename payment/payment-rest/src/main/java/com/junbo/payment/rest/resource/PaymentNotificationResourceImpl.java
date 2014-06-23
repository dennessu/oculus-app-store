/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.provider.PaymentProvider;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.resource.PaymentNotificationResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;

/**
 * Created by Administrator on 14-6-23.
 */
public class PaymentNotificationResourceImpl implements PaymentNotificationResource {
    @Autowired
    private PaymentTransactionService paymentService;

    @Override
    public Promise<Response> receiveAdyenNotification(String request) {
        return paymentService.processNotification(PaymentProvider.Adyen, request)
                .then(new Promise.Func<PaymentTransaction, Promise<Response>>() {
                    @Override
                    public Promise<Response> apply(PaymentTransaction paymentTransaction) {
                        return Promise.pure(Response
                                .status(Response.Status.OK)
                                .entity("[accepted]")
                                .build());
                    }
                });
    }
}
