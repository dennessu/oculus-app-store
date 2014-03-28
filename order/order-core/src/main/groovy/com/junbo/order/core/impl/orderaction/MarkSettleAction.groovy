package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Created by fzhang on 14-3-25.
 */
@CompileStatic
@TypeChecked
class MarkSettleAction implements Action {

    @Resource(name = 'orderRepository')
    OrderRepository repo

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order

        order.tentative = false
        def latest = repo.getOrder(order.id.value)

        if (latest?.tentative) {
            order.tentative = false
            repo.updateOrder(order, true)
            return Promise.pure(null)
        }

        throw AppErrors.INSTANCE.orderNotTentative().exception()
    }
}
