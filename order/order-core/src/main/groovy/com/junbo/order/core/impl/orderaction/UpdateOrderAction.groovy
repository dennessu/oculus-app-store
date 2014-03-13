package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 3/7/14.
 */
@Component('updateOrderAction')
@CompileStatic
class UpdateOrderAction implements Action {

    @Autowired
    OrderRepository orderRepository

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        // TODO build the order event according to the scenario based on the context
        def context = ActionUtils.getOrderActionContext(actionContext)
        def orderEvent = new OrderEvent()
        // orderEvent.action = context.action?.toString()
        orderEvent.status = EventStatus.OPEN.toString()
        def orderWithId = orderRepository.updateOrder(
                context.orderServiceContext.order)
        context.orderServiceContext.setOrder(orderWithId)
        return Promise.pure(null)
    }
}
