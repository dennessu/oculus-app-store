package com.junbo.order.mock

import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.enums.PIStatus
import com.junbo.payment.spec.enums.PIType
import com.junbo.payment.spec.model.PageMetaData
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.core.Response

/**
 * Created by chriszhu on 2/11/14.
 */
@CompileStatic
@Scope('prototype')
@Component('mockPaymentInstrumentResource')
class MockPaymentInstrumentResource extends BaseMock implements PaymentInstrumentResource {

    @Override
    Promise<PaymentInstrument> postPaymentInstrument(UserId userId, PaymentInstrument request) {
        return null
    }

    @Override
    Promise<PaymentInstrument> getById(UserId userId, PaymentInstrumentId paymentInstrumentId) {
        def pi = new PaymentInstrument()
        pi.id = paymentInstrumentId.value
        pi.isValidated = true
        pi.status = PIStatus.ACTIVE
        pi.type = PIType.CREDITCARD
        pi.userId = userId.value
        return Promise.pure(pi)
    }

    @Override
    Promise<Response> delete(UserId userId, PaymentInstrumentId paymentInstrumentId) {
        return null
    }

    @Override
    Promise<PaymentInstrument> update(UserId userId,
                                      PaymentInstrumentId paymentInstrumentId, PaymentInstrument request) {
        return null
    }

    @Override
    Promise<Results<PaymentInstrument>> searchPaymentInstrument(
            UserId userId,
            PaymentInstrumentSearchParam searchParam,
            PageMetaData pageMetadata) {
        return null
    }


}
