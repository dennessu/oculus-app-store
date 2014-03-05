/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.mock

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentTransaction

/**
 * Created by xmchen on 14-2-27.
 */
class MockPaymentFacadeImpl implements PaymentFacade {
    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long piId) {
        PaymentInstrument pi = new PaymentInstrument()
        pi.setId(54321)
        pi.setUserId(12345)
        return Promise.pure(pi)
    }

    @Override
    Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        return null
    }

    @Override
    Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        return null
    }
}
