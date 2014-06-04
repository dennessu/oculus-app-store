package com.junbo.order.core.impl.orderaction.context

import com.junbo.order.spec.model.enums.OrderActionType
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by chriszhu on 3/11/14.
 */
@CompileStatic
@TypeChecked
class CreateOrderActionContext {
    OrderActionType orderActionType
}
