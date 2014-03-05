package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.orderaction.context.BaseContext

/**
 * Created by LinYi on 14-2-21.
 */
class GetOrderAction implements OrderAction<BaseContext> {
    @Override
    Promise<BaseContext> execute(BaseContext context) {
        if (context.orderServiceContext.orderId != null) {
            // get Order by id
            def order = context.orderServiceContext.orderRepository.getOrder(context.orderServiceContext.orderId)
            context.orderServiceContext.setOrder(order)
        }

        return Promise.pure(context)
    }
}
