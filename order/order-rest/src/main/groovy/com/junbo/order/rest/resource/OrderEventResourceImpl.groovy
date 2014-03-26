package com.junbo.order.rest.resource
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderEventService
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.resource.OrderEventResource
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.core.HttpHeaders
/**
 * Created by chriszhu on 3/12/14.
 */
@CompileStatic
@Scope('prototype')
@Component('defaultOrderEventResource')
class OrderEventResourceImpl implements OrderEventResource {
    @Resource
    OrderEventService orderEventService

    @Override
    Promise<Results<OrderEvent>> getOrderEvents(OrderId orderId, HttpHeaders headers) {
        return orderEventService.getOrderEvents(orderId.value).then { List<OrderEvent> orderEvents ->
            Results<OrderEvent> results = new Results<>()
            results.setItems(orderEvents)
            return Promise.pure(results)
        }
    }

    @Override
    Promise<OrderEvent> createOrderEvent(OrderEvent orderEvent, HttpHeaders headers) {
        return null
    }
}
