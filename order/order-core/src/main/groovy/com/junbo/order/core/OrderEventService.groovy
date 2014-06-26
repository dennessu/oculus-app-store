package com.junbo.order.core

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam

/**
 * Created by LinYi on 14-3-20.
 */
interface OrderEventService {
    Promise<List<OrderEvent>> getOrderEvents(Long orderId, PageParam pageParam)

    Promise<OrderEvent> recordEventHistory(OrderEvent event, OrderServiceContext orderServiceContext)

    Promise<OrderEvent> recordBillingHistory(OrderEvent event, OrderServiceContext orderServiceContext)
}
