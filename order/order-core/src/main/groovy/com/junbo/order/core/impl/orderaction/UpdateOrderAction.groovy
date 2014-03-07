package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.EventStatus
import com.junbo.order.spec.model.OrderEvent
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by chriszhu on 3/7/14.
 */
class UpdateOrderAction {

    @Autowired
    OrderRepository orderRepository

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        // TODO build the order event according to the scenario based on the context
        def context = ActionUtils.getOrderActionContext(actionContext)
        def orderEvent = new OrderEvent()
        orderEvent.action = context.action?.toString()
        orderEvent.status = EventStatus.OPEN.toString()
        def orderWithId = orderRepository.updateOrder(
                context.orderServiceContext.order, orderEvent)
        context.orderServiceContext.setOrder(orderWithId)
        return Promise.pure(null)
    }
}
