package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.spec.model.enums.OrderActionType
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 6/5/2014.
 */
@CompileStatic
class OrderEventStatusCheckAction implements Action {

    private OrderActionType orderActionType

    void setOrderActionType(OrderActionType orderActionType) {
        assert orderActionType != null
        this.orderActionType = orderActionType
    }

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def event = context.orderServiceContext.orderEvent
        if (event?.action == orderActionType.name()) {
            return Promise.pure(new ActionResult(event.status))
        }
        return Promise.pure(new ActionResult('__NONE'))
    }
}
