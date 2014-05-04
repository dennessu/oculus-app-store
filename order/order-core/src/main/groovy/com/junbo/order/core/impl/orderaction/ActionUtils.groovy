package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.context.CreateOrderActionContext
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.core.impl.orderaction.context.OrderActionResult
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 14-3-6.
 */
@CompileStatic
final class ActionUtils {

    private ActionUtils() {
    }

    static final String SCOPE_ORDER_ACTION_CONTEXT = 'ORDER_ACTION_CONTEXT'
    static final String SCOPE_CREATE_ORDER_ACTION_CONTEXT = 'CREATE_ORDER_ACTION_CONTEXT'
    static final String DATA_ORDER_ACTION_RESULT = 'ORDER_ACTION_RESULT'

    static final String REQUEST_FLOW_NAME = 'FLOW_NAME'
    static final String REQUEST_ORDER_ID = 'ORDER_ID'

    static OrderActionContext getOrderActionContext(ActionContext actionContext) {
        return (OrderActionContext)actionContext?.requestScope[SCOPE_ORDER_ACTION_CONTEXT]
    }

    static CreateOrderActionContext getCreateOrderActionContext(ActionContext actionContext) {
        return (CreateOrderActionContext)actionContext?.requestScope[SCOPE_CREATE_ORDER_ACTION_CONTEXT]
    }

    static OrderActionResult getOrderActionResult(ActionResult actionResult) {
        return (OrderActionResult)actionResult?.data[DATA_ORDER_ACTION_RESULT]
    }

    static void putOrderActionContext(OrderActionContext context, ActionContext actionContext) {
        actionContext?.requestScope[SCOPE_ORDER_ACTION_CONTEXT] = context
    }

    static String getFlowName(ActionContext actionContext) {
        return actionContext?.requestScope[REQUEST_FLOW_NAME]
    }

    static void putFlowName(String flowName, ActionContext actionContext) {
        actionContext?.requestScope[REQUEST_FLOW_NAME] = flowName
    }

    static Map<String, Object> initRequestScope(OrderServiceContext context) {
        def orderActionContext = new OrderActionContext()
        orderActionContext.orderServiceContext = context
        def result = [:]
        result[SCOPE_ORDER_ACTION_CONTEXT] = orderActionContext
        return result
    }
}
