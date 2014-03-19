package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.error.AppErrors
import com.junbo.payment.spec.enums.PIStatus
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 3/10/14.
 */
@CompileStatic
@Component('validatePaymentInstrumentsAction')
class ValidatePaymentInstrumentsAction implements com.junbo.langur.core.webflow.action.Action {

    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        // validate payments
        return orderServiceContextBuilder.getPaymentInstruments(
                context.orderServiceContext).syncThen { List<PaymentInstrument> pis ->
            if (pis.find { PaymentInstrument pi -> pi.status == PIStatus.ACTIVE.toString() } == null) {
                throw AppErrors.INSTANCE.paymentStatusInvalid().exception()
            }
            return null
        }
    }

}
