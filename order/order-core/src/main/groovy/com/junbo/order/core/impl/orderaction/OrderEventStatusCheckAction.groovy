package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.model.enums.OrderActionType
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by fzhang on 6/5/2014.
 */
@CompileStatic
class OrderEventStatusCheckAction implements Action {

    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    private OrderActionType orderActionType

    void setOrderActionType(OrderActionType orderActionType) {
        assert orderActionType != null
        this.orderActionType = orderActionType
    }

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        def event = context.orderServiceContext.orderEvent
        if (event == null) {
            return Promise.pure(new ActionResult('__NONE'))
        }
        return orderServiceContextBuilder.getBalances(context.orderServiceContext).then { List<Balance> balances ->
            // Check event status from billing and fulfillment
            if (balances != null) {
                orderInternalService.checkOrderEventStatus(order, event, balances)
                if (event?.action == orderActionType.name()) {
                    return Promise.pure(new ActionResult(event.status))
                }
            }
            return Promise.pure(new ActionResult('__NONE'))
        }
    }
}
