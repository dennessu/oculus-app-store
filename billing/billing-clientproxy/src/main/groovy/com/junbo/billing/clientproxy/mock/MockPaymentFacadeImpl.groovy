/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.mock

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.enums.PaymentStatus
import com.junbo.payment.spec.enums.PaymentType
import com.junbo.payment.spec.model.Address
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentTransaction
import groovy.transform.CompileStatic

/**
 * Created by xmchen on 14-2-27.
 */
@CompileStatic
class MockPaymentFacadeImpl implements PaymentFacade {
    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long piId) {
        PaymentInstrument pi = new PaymentInstrument()
        pi.setId(piId)
        pi.setUserId(12345L)
        Address address = new Address()
        address.id = 999999
        address.addressLine1 = '7462 Kearny Street'
        address.city = 'Commerce City'
        address.state = 'CA'
        address.postalCode = '96045'
        address.country = 'US'
        pi.setAddress(address)
        return Promise.pure(pi)
    }

    @Override
    Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        request.setId(1111111L)
        request.setMerchantAccount('JUNBO')
        request.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.name())
        request.setType(PaymentType.CHARGE.name())
        request.setPaymentProvider('MOCK')
        return Promise.pure(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        request.setId(33333333L)
        request.setMerchantAccount('JUNBO')
        request.setStatus(PaymentStatus.AUTHORIZED.name())
        request.setType(PaymentType.AUTHORIZE.name())
        request.setPaymentProvider('MOCK')
        return Promise.pure(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentCapture(Long paymentId, PaymentTransaction request) {
        request.setId(paymentId)
        request.setMerchantAccount('JUNBO')
        request.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.name())
        request.setType(PaymentType.CAPTURE.name())
        request.setPaymentProvider('MOCK')
        return Promise.pure(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentConfirm(Long paymentId, PaymentTransaction request) {
        request.setId(paymentId)
        request.setMerchantAccount('JUNBO')
        request.setStatus(PaymentStatus.SETTLED.name())
        request.setType(PaymentType.CHARGE.name())
        request.setPaymentProvider('MOCK')
        return Promise.pure(request)
    }

    @Override
    Promise<PaymentTransaction> getPayment(Long paymentId) {
        PaymentTransaction paymentTransaction = new PaymentTransaction()
        paymentTransaction.setId(paymentId)
        paymentTransaction.setMerchantAccount('JUNBO')
        paymentTransaction.setStatus(PaymentStatus.SETTLED.name())
        paymentTransaction.setType(PaymentType.CHARGE.name())
        paymentTransaction.setPaymentProvider('MOCK')
        return Promise.pure(paymentTransaction)
    }
}
