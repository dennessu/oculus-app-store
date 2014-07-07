/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.payment.impl

import com.junbo.common.id.PaymentId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.payment.spec.model.PaymentCallbackParams
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.resource.PaymentCallbackResource
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.core.Response

/**
 * Created by chriszhu on 2/11/14.
 */
@Component('orderPaymentFacade')
@CompileStatic
@TypeChecked
class PaymentFacadeImpl implements PaymentFacade {

    @Resource(name='order.paymentInstrumentClient')
    PaymentInstrumentResource paymentInstrumentResource

    @Resource(name='order.paymentCallbackClient')
    PaymentCallbackResource paymentCallbackResource

    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long paymentInstrumentId) {
        return paymentInstrumentResource.getById(new PaymentInstrumentId(paymentInstrumentId))
    }

    @Override
    Promise<Response> postPaymentProperties(Long paymentId, PaymentCallbackParams properties) {
        return paymentCallbackResource.postPaymentProperties(new PaymentId(paymentId), properties)
    }
}
