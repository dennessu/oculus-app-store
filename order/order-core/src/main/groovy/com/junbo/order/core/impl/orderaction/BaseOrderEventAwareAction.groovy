package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.webflow.action.Action
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
/**
 * Created by chriszhu on 3/13/14.
 */
abstract class BaseOrderEventAwareAction implements Action {
    OrderActionType orderActionType
    EventStatus eventStatus
}