package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class CreateOrderAction implements Action {
    @Autowired
    OrderRepository orderRepository

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        // TODO build the order event according to the scenario based on the context
        def context = ActionUtils.getOrderActionContext(actionContext)
        def orderEvent = new OrderEvent()
        // orderEvent.action = context.action?.toString() // todo set the action
        orderEvent.status = EventStatus.OPEN.toString()
        def orderWithId = orderRepository.createOrder(
                context.orderServiceContext.order, orderEvent)
        context.orderServiceContext.setOrder(orderWithId)
        return Promise.pure(null)
    }
}
