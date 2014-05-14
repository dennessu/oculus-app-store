package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Created by chriszhu on 5/14/14.
 */
@CompileStatic
@TypeChecked
@Component("cancelOrderAction")
class CancelOrderAction extends BaseOrderEventAwareAction {

    @Override
    @OrderEventAwareBefore(action = 'CancelOrderAction')
    @OrderEventAwareAfter(action = 'CancelOrderAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        return Promise.pure(null)
    }
}
