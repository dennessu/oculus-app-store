package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.webflow.action.Action
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 3/13/14.
 */
@CompileStatic
abstract class BaseOrderEventAwareAction implements Action {
    OrderActionType orderActionType
    EventStatus eventStatus
}