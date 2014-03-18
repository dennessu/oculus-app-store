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

/**
 * Created by xmchen on 14-2-27.
 */
class MockPaymentFacadeImpl implements PaymentFacade {
    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long userId, Long piId) {
        PaymentInstrument pi = new PaymentInstrument()
        pi.setId(piId)
        pi.setUserId(userId)
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
        request.setPaymentId(1111111)
        request.setMerchantAccount('JUNBO')
        request.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.name())
        request.setType(PaymentType.CHARGE.name())
        request.setPaymentProvider('MOCK')
        return Promise.pure(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        request.setPaymentId(33333333)
        request.setMerchantAccount('JUNBO')
        request.setStatus(PaymentStatus.AUTHORIZED.name())
        request.setType(PaymentType.AUTHORIZE.name())
        request.setPaymentProvider('MOCK')
        return Promise.pure(request)
    }

    @Override
    Promise<PaymentTransaction> postPaymentCapture(Long paymentId, PaymentTransaction request) {
        request.setPaymentId(paymentId)
        request.setMerchantAccount('JUNBO')
        request.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.name())
        request.setType(PaymentType.CAPTURE.name())
        request.setPaymentProvider('MOCK')
        return Promise.pure(request)
    }
}
