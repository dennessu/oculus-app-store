package com.junbo.order.core.impl.orderaction.context

import com.junbo.order.spec.model.EventStatus
import com.junbo.order.spec.model.OrderAction

/**
 * Created by chriszhu on 3/4/14.
 */
class CreateOrderActionContext extends BaseContext {
    OrderAction action
    EventStatus status
}
