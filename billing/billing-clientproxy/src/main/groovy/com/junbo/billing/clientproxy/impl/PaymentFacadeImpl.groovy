/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentTransaction
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import com.junbo.payment.spec.resource.PaymentTransactionResource
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by xmchen on 2/20/14.
 */
@CompileStatic
class PaymentFacadeImpl implements PaymentFacade {

    @Resource(name = 'billingPaymentInstrumentClient')
    private PaymentInstrumentResource paymentInstrumentResource

    @Resource(name = 'billingPaymentTransactionClient')
    private PaymentTransactionResource paymentTransactionResource

    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long piId) {
        return paymentInstrumentResource.getById(new PaymentInstrumentId(piId))
    }

    @Override
    Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        return paymentTransactionResource.postPaymentCharge(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        return paymentTransactionResource.postPaymentAuthorization(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentCapture(Long paymentId, PaymentTransaction request) {
        return paymentTransactionResource.postPaymentCapture(paymentId, request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentConfirm(Long paymentId, PaymentTransaction request) {
        return paymentTransactionResource.postPaymentConfirm(paymentId, request)
    }

    @Override
    Promise<PaymentTransaction> getPayment(Long paymentId) {
        return paymentTransactionResource.getPayment(paymentId)
    }
}
