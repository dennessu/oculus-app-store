/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.mock;

import com.junbo.billing.clientproxy.PaymentFacade;
import com.junbo.common.id.PIType;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

/**
 * Created by xmchen on 14-5-19.
 */
public class MockPaymentFacade implements PaymentFacade {
    @Override
    public Promise<PaymentInstrument> getPaymentInstrument(Long piId) {
        PaymentInstrument pi = new PaymentInstrument();
        pi.setId(piId);
        pi.setBillingAddressId(11111L);
        pi.setType(PIType.CREDITCARD.getId());

        return Promise.pure(pi);
    }

    @Override
    public Promise<PaymentTransaction> getPayment(Long paymentId) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        request.setId(11111L);
        request.setStatus(PaymentStatus.SETTLING.name());
        return Promise.pure(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        request.setId(11111L);
        request.setStatus(PaymentStatus.AUTHORIZED.name());
        return Promise.pure(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCapture(Long paymentId, PaymentTransaction request) {
        request.setId(11111L);
        request.setStatus(PaymentStatus.SETTLING.name());
        return Promise.pure(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentConfirm(Long paymentId, PaymentTransaction request) {
        request.setId(11111L);
        request.setStatus(PaymentStatus.SETTLED.name());
        return Promise.pure(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCheck(Long paymentId) {
        PaymentTransaction request = new PaymentTransaction();
        request.setId(paymentId);
        request.setStatus(PaymentStatus.REFUNDED.name());
        return Promise.pure(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentRefund(Long paymentId, PaymentTransaction request) {
        request.setId(paymentId);
        request.setStatus(PaymentStatus.SETTLED.name());
        return Promise.pure(request);
    }
}
