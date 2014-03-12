package com.junbo.order.core.impl.orderaction.context

import com.junbo.order.db.entity.enums.EventStatus

/**
 * Created by chriszhu on 3/11/14.
 */
class OrderActionResult {
    OrderActionContext orderActionContext
    EventStatus returnedEventStatus
}
