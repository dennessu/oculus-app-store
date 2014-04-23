/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

/**
 * Created by xmchen on 2/20/14.
 */
public interface PaymentFacade {
    Promise<PaymentInstrument> getPaymentInstrument(Long piId);

    Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request);

    Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request);

    Promise<PaymentTransaction> postPaymentCapture(Long paymentId, PaymentTransaction request);

    Promise<PaymentTransaction> postPaymentConfirm(Long paymentId, PaymentTransaction request);
}
