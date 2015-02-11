package com.junbo.billing.listener.clientproxy

import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.resource.OrderEventResource
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by xmchen on 14-6-3.
 */
@CompileStatic
class OrderFacadeImpl implements OrderFacade {
    @Resource(name = 'billingOrderEventClient')
    private OrderEventResource orderEventResource

    @Override
    Promise<OrderEvent> postOrderEvent(OrderEvent orderEvent) {
        return orderEventResource.createOrderEvent(orderEvent)
    }
}
