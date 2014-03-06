package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.spec.model.EventStatus
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class CreateOrderAction implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        // TODO build the order event according to the scenario based on the context
        def context = ActionUtils.getOrderActionContext(actionContext)
        def orderEvent = new OrderEvent()
        // orderEvent.action = context.action?.toString() // todo set the action
        orderEvent.status = EventStatus.OPEN.toString()
        def orderWithId = context.orderServiceContext.orderRepository.createOrder(
                context.orderServiceContext.order, orderEvent)
        context.orderServiceContext.setOrder(orderWithId)
        return Promise.pure(null)
    }
}
