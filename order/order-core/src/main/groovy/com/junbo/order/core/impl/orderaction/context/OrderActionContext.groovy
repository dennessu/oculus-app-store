package com.junbo.order.core.impl.orderaction.context
import com.junbo.order.core.FlowType
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.db.entity.enums.OrderActionType
import groovy.transform.CompileStatic
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
class OrderActionContext {

    OrderServiceContext orderServiceContext
    Long orderId
    FlowType flowType
    OrderActionType orderActionType
    UUID trackingUuid
}
