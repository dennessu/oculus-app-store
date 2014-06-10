package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Created by chriszhu on 5/14/14.
 */
@CompileStatic
@TypeChecked
@Component("refundOrderAction")
class RefundOrderAction extends BaseOrderEventAwareAction {

    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    @Override
    @OrderEventAwareBefore(action = 'RefundOrderAction')
    @OrderEventAwareAfter(action = 'RefundOrderAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order

        assert(order != null)
        return orderInternalService.refundOrder(order).then { Order o ->
            return Promise.pure(null)
        }
    }
}
