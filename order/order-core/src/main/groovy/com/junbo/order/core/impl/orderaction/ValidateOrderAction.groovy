package com.junbo.order.core.impl.orderaction

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import javax.annotation.Resource

/**
 * Created by fzhang on 14-3-27.
 */
@CompileStatic
class ValidateOrderAction implements Action {

    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

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
        def promise = builder.getCurrency(context.orderServiceContext)
        return promise.then { com.junbo.identity.spec.v1.model.Currency currency ->
            if (currency == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('currency', 'can not get valid currency').exception()
            }
            return orderInternalService.validateUserPersonalInfo(context.orderServiceContext).then {
                return Promise.pure(null)
            }
        }
    }
}
