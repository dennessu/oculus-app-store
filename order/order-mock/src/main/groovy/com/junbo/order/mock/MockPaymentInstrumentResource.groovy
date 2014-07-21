package com.junbo.order.mock

import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PageMetaData
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam
import javax.ws.rs.core.Response

/**
 * Created by chriszhu on 2/11/14.
 */
@CompileStatic
@TypeChecked
@Component('mockPaymentInstrumentResource')
class MockPaymentInstrumentResource extends BaseMock implements PaymentInstrumentResource {

    @Override
    Promise<PaymentInstrument> postPaymentInstrument(PaymentInstrument request) {
        return null
    }

    @Override
    Promise<PaymentInstrument> getById(PaymentInstrumentId paymentInstrumentId) {
        def pi = new PaymentInstrument()
        pi.id = paymentInstrumentId.value
        pi.isValidated = true
        pi.isActive = true
        pi.type = generateLong()
        return Promise.pure(pi)
    }

    @Override
    Promise<Response> delete(PaymentInstrumentId paymentInstrumentId) {
        return null
    }

    @Override
    Promise<PaymentInstrument> update(PaymentInstrumentId paymentInstrumentId, PaymentInstrument request) {
        return null
    }

    @Override
    Promise<Results<PaymentInstrument>> searchPaymentInstrument(
            @BeanParam PaymentInstrumentSearchParam searchParam, @BeanParam PageMetaData pageMetadata) {
        return null
    }
}
