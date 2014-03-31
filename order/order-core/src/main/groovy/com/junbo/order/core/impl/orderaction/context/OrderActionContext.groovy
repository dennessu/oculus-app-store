package com.junbo.order.core.impl.orderaction.context
import com.junbo.order.core.impl.order.OrderServiceContext
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class OrderActionContext {

    OrderServiceContext orderServiceContext
    // For order event
    UUID trackingUuid
}
