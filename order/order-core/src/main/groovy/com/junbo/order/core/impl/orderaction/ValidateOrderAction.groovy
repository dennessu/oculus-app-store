package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.db.entity.enums.OrderType
import com.junbo.order.spec.model.OrderItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by fzhang on 14-3-27.
 */
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

        orderValidator.notNull(order.type, 'type').validEnumString(order.type, 'type', OrderType)

        // todo validate country
        orderValidator.validCurrency(order.currency, 'currency')

        order.orderItems?.eachWithIndex { OrderItem item, int index ->
            orderValidator.notNull(item.offer, "orderItems[${index}].offer")
            orderValidator.between(item.quantity, 1, 100, "orderItems[${index}].quantity")
        }
        return Promise.pure(null)
    }
}
