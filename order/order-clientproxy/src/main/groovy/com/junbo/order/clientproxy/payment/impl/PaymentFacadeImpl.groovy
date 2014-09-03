/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.payment.impl
import com.junbo.common.error.AppError
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.error.ErrorUtils
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.resource.PaymentCallbackResource
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentFacadeImpl)

    @Override
    Promise<PaymentInstrument> getPaymentInstrument(Long paymentInstrumentId) {
        return paymentInstrumentResource.getById(new PaymentInstrumentId(paymentInstrumentId)).recover { Throwable ex ->
            LOGGER.error('name=PaymentFacadeImpl_Get_PI_Error', ex)
            throw convertError(ex, paymentInstrumentId).exception()
        }.then { PaymentInstrument pi ->
            if (pi == null) {
                throw AppErrors.INSTANCE.paymentInstrumentNotFound(paymentInstrumentId.toString()).exception()
            }
            return Promise.pure(pi)
        }
    }

    @Override
    Promise<Response> postPaymentProperties(String request) {
        return paymentCallbackResource.postPaymentProperties(request)
    }

    private AppError convertError(Throwable error, Long paymentInstrumentId) {
        AppError e = ErrorUtils.toAppError(error)
        if (e != null && e.httpStatusCode < 500 ) {
            return AppErrors.INSTANCE.paymentInstrumentNotFound(paymentInstrumentId.toString())
        }
        if (e != null) {
            return AppErrors.INSTANCE.paymentConnectionError(e)
        }
        return AppErrors.INSTANCE.paymentConnectionError(error.message)
    }
}
