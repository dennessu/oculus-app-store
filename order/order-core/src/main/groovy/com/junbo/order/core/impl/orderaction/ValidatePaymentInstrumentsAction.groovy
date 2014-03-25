package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.error.AppErrors
import com.junbo.payment.spec.enums.PIStatus
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatePaymentInstrumentsAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        // validate payments
        return orderServiceContextBuilder.getPaymentInstruments(
                context.orderServiceContext).syncRecover {
            LOGGER.error('name=Validate_Payment_Instrument_Fail')
            throw AppErrors.INSTANCE.paymentConnectionError().exception()
        }.syncThen { List<PaymentInstrument> pis ->
            if (CollectionUtils.isEmpty(pis)) {
                LOGGER.info('name=Validate_Payment_Instrument_Not_Found')
                return null
            }
            def invalidPi = pis.find { PaymentInstrument pi -> pi.status != PIStatus.ACTIVE.toString() }
            if (invalidPi != null) {
                throw AppErrors.INSTANCE.paymentInstrumentStatusInvalid(invalidPi.id.toString()).exception()
            }
            return null
        }
    }
}
