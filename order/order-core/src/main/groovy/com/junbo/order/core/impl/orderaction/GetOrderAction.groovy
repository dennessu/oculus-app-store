package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.orderaction.context.OrderActionContext

/**
 * Created by LinYi on 14-2-21.
 */
class GetOrderAction implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        if (context.orderServiceContext.orderId != null) {
            // get Order by id
            def order = context.orderServiceContext.orderRepository.getOrder(context.orderServiceContext.orderId)
            context.orderServiceContext.setOrder(order)
        }

        return Promise.pure(ActionUtils.DEFAULT_RESULT)
    }
}
