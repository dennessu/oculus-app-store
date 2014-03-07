package com.junbo.order.core.impl.orderaction
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.spec.error.AppErrors
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class ValidateAction implements Action {

    @Autowired
    IdentityFacade identityFacade
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        validateUser(context).syncThen {
            ActionUtils.DEFAULT_RESULT
        }
    }

    private Promise<Void> validateUser(OrderActionContext context) {
        def order = context.orderServiceContext.order
        validatePayment(context) // validate payment
        // validate user
        return identityFacade.getUser(order.user.value).then { User user ->
            if (user.status != 'ACTIVE') {
                throw AppErrors.INSTANCE.userStatusInvalid().exception()
            }
        }
    }

    private void validatePayment(OrderActionContext context) {
        if (orderServiceContextBuilder.getPaymentInstruments(
                context.orderServiceContext)?.find { PaymentInstrument paymentInstrument ->
            paymentInstrument.status == 'ACTIVE'
        } == null) {
            throw AppErrors.INSTANCE.paymentStatusInvalid().exception()
        }
    }


}
