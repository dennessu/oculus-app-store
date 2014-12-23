package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.error.AppErrors
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 3/10/14.
 */
@CompileStatic
@TypeChecked
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
                context.orderServiceContext).syncRecover { Throwable ex ->
            LOGGER.error('name=Validate_Payment_Instrument_Fail')
            throw ex
        }.syncThen { List<PaymentInstrument> pis ->
            if (CollectionUtils.isEmpty(pis)) {
                LOGGER.info('name=Validate_Payment_Instrument_Not_Found')
                return null
            }
            // TODO: need double confirm whether this is the way to validate pi
            // TODO: validate pi after the pi status design is locked down.
            // def invalidPi = pis.find { PaymentInstrument pi -> !pi.isValidated }
            if (!CollectionUtils.isEmpty(context.orderServiceContext.order.payments) &&
                    CollectionUtils.isEmpty(pis)) {
                throw AppErrors.INSTANCE.paymentInstrumentStatusInvalid(
                        context.orderServiceContext.order.payments[0].paymentInstrument.toString()).exception()
            }
            return null
        }
    }
}
