package com.junbo.order.core.impl.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderFlow
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.GetOrderAction
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.spec.model.Order

/**
 * Created by LinYi on 14-2-21.
 */
class GetOrderFlow implements OrderFlow {
    @Override
    UUID getName() {
        return UUID.fromString('9D0B9BCF-0425-44BB-9F24-202E6783B72A')
    }

    @Override
    Promise<List<Order>> execute(OrderServiceContext order) {
        OrderActionContext context = new OrderActionContext()
        context.setOrderServiceContext(order)

        def currentAction = new GetOrderAction()
        Promise<OrderActionContext> promise = currentAction.execute(context)

        def result = promise.wrapped().get()
        return [((OrderActionContext)result).orderServiceContext.order]
    }
}
