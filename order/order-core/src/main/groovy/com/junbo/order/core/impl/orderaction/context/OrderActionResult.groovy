package com.junbo.order.core.impl.orderaction.context

import com.junbo.order.spec.model.enums.EventStatus
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 3/11/14.
 */
@CompileStatic
class OrderActionResult {
    OrderActionContext orderActionContext
    EventStatus returnedEventStatus
}
