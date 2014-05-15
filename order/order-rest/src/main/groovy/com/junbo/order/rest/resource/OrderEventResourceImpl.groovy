package com.junbo.order.rest.resource
import com.junbo.common.id.OrderEventId
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderEventService
import com.junbo.order.core.OrderService
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.resource.OrderEventResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
/**
 * Created by chriszhu on 3/12/14.
 */
@CompileStatic
@TypeChecked
@Scope('prototype')
@Component('defaultOrderEventResource')
class OrderEventResourceImpl implements OrderEventResource {
    @Resource
    OrderEventService orderEventService

    @Resource
    OrderService orderService

    @Resource
    OrderValidator orderValidator

    @Override
    Promise<Results<OrderEvent>> getOrderEvents(OrderId orderId, PageParam pageParam) {
        return orderEventService.getOrderEvents(orderId.value, pageParam).then { List<OrderEvent> orderEvents ->
            Results<OrderEvent> results = new Results<>()
            results.setItems(orderEvents)
            return Promise.pure(results)
        }
    }

    @Override
    Promise<OrderEvent> createOrderEvent(OrderEvent orderEvent, @Context HttpHeaders headers) {
        orderValidator.notNull(orderEvent, 'orderEvent').notNull(orderEvent.order, 'orderId')
        return orderService.updateOrderByOrderEvent(orderEvent).then { OrderEvent event ->
            return Promise.pure(event)
        }
    }

    @Override
    Promise<OrderEvent> getOrderEvent(@PathParam('orderEventId') OrderEventId orderEventId) {
        return null
    }
}
