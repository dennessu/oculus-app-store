package com.junbo.order.mock

import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PageMetaData
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam
import com.junbo.payment.spec.model.ResultList
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope

import javax.ws.rs.core.Response

/**
 * Created by chriszhu on 2/11/14.
 */
@CompileStatic
@Scope('prototype')
class MockPaymentInstrumentResource extends BaseMock implements PaymentInstrumentResource {

    @Override
    Promise<PaymentInstrument> postPaymentInstrument(PaymentInstrument request) {
        return null
    }

    @Override
    Promise<PaymentInstrument> getById(Long paymentInstrumentId) {
        def pi = new PaymentInstrument()
        pi.id = paymentInstrumentId
        pi.isValidated = true
        pi.status = 'ACTIVE'
        pi.type = 'CREDIT_CARD'
        return Promise.pure(pi)
    }

    @Override
    Promise<Response> delete(Long paymentInstrumentId) {
        return null
    }

    @Override
    Promise<PaymentInstrument> update(Long paymentInstrumentId, PaymentInstrument request) {
        return null
    }

    @Override
    Promise<ResultList<PaymentInstrument>> searchPaymentInstrument(
            PaymentInstrumentSearchParam searchParam,
            PageMetaData pageMetadata) {
        return null
    }
}
