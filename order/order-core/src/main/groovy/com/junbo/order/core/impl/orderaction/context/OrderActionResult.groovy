package com.junbo.order.core.impl.orderaction.context

import com.junbo.common.error.AppError
import com.junbo.order.spec.model.enums.EventStatus
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 3/11/14.
 */
@CompileStatic
class OrderActionResult {
    OrderActionContext orderActionContext
    EventStatus returnedEventStatus
    AppError exception
}
