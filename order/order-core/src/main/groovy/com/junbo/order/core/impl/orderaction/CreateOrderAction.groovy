package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.orderaction.context.CreateOrderActionContext
import com.junbo.order.spec.model.EventStatus
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class CreateOrderAction implements OrderAction<CreateOrderActionContext> {

    @Override
    Promise<CreateOrderActionContext> execute(CreateOrderActionContext context) {
        // TODO build the order event according to the scenario based on the context
        def orderEvent = new OrderEvent()
        orderEvent.action = context.action?.toString()
        orderEvent.status = EventStatus.OPEN.toString()
        def orderWithId = context.orderServiceContext.orderRepository.createOrder(
                context.orderServiceContext.order, orderEvent)
        context.orderServiceContext.setOrder(orderWithId)
    }
}
