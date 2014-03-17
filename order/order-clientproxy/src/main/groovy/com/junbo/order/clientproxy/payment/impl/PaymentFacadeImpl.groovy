/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.payment.impl

import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by chriszhu on 2/11/14.
 */
@Component('paymentFacade')
@CompileStatic
class PaymentFacadeImpl implements PaymentFacade {

    @Resource(name='paymentInstrumentClient')
    PaymentInstrumentResource paymentInstrumentResource

    @Override
    Promise<PaymentInstrument> getPaymentInstrument(UserId userId, Long paymentInstrumentId) {
        return paymentInstrumentResource.getById(userId, new PaymentInstrumentId(paymentInstrumentId))
    }
}
