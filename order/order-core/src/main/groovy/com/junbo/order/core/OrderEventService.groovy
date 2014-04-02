package com.junbo.order.core

import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam

/**
 * Created by LinYi on 14-3-20.
 */
interface OrderEventService {
    Promise<List<OrderEvent>> getOrderEvents(Long orderId, PageParam pageParam)
}
