package com.junbo.order.rest.resource
import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.resource.OrderEventResource

import javax.ws.rs.core.HttpHeaders
/**
 * Created by chriszhu on 3/12/14.
 */
class OrderEventResourceImpl implements OrderEventResource {

    @Override
    Promise<List<OrderEvent>> getOrderEvents(OrderId orderId, HttpHeaders headers) {
        return null
    }

    @Override
    Promise<OrderEvent> createOrderEvent(OrderEvent orderEvent, HttpHeaders headers) {
        return null
    }
}
