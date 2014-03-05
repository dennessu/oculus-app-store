/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.payment.impl

import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by chriszhu on 2/11/14.
 */
@Component('paymentFacade')
class PaymentFacadeImpl implements PaymentFacade {

    @Resource(name='paymentInstrumentClient')
    PaymentInstrumentResource paymentInstrumentResource

    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long paymentInstrumentId) {
        return paymentInstrumentResource.getById(paymentInstrumentId)
    }
}
