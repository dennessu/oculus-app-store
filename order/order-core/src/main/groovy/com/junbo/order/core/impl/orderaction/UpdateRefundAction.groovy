package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.enums.EventStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Capture action.
 */
@CompileStatic
@TypeChecked
class UpdateRefundAction extends BaseOrderEventAwareAction {
    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    @Override
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        assert(order != null)
        return orderInternalService.checkAndUpdateRefund(order, context.orderServiceContext).syncThen { Order o ->
            context.orderServiceContext.order = o
            return CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.COMPLETED)
        }
    }
}
