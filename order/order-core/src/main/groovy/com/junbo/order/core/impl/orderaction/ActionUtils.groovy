package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.context.OrderActionContext

/**
 * Created by fzhang on 14-3-6.
 */
final class ActionUtils {

    private ActionUtils() {
    }

    static final SCOPE_ORDER_ACTION_CONTEXT = 'ORDER_ACTION_CONTEXT'

    static final ActionResult DEFAULT_RESULT = new ActionResult('')

    static OrderActionContext getOrderActionContext(ActionContext actionContext) {
        return actionContext.requestScope[SCOPE_ORDER_ACTION_CONTEXT]
    }

    static OrderActionContext putOrderActionContext(OrderActionContext context, ActionContext actionContext) {
        actionContext.requestScope[SCOPE_ORDER_ACTION_CONTEXT] = context
    }

    static Map<String, Object> initRequestScope(OrderServiceContext context, Map<String, Object> args) {
        def orderActionContext = new OrderActionContext()
        orderActionContext.orderServiceContext = context
        def result = (args == null) ? [:] : (Map<String, Object>) args.clone()
        result[SCOPE_ORDER_ACTION_CONTEXT] = orderActionContext
        return result
    }
}
