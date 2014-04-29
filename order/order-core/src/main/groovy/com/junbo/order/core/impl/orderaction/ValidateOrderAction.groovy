package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Created by fzhang on 14-3-27.
 */
@CompileStatic
class ValidateOrderAction implements Action {

    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order

        orderValidator.notNull(order, 'order')
        orderValidator.notNull(order.user, 'user')

        order.orderItems?.eachWithIndex { OrderItem item, int index ->
            orderValidator.notNull(item.offer, "orderItems[${index}].offer")
            orderValidator.between(item.quantity, 1, 100, "orderItems[${index}].quantity")
        }

        // todo validate country
        return orderValidator.validCurrency(order.currency.value, 'currency').syncThen {
            return null
        }
//        orderValidator.validWebPaymentUrls(order.paymentInstruments,
//                order.successRedirectUrl, order.cancelRedirectUrl)
    }
}
