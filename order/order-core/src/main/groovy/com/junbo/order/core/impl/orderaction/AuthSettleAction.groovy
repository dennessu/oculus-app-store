package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.CoreBuilder
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Auth Settle Action.
 */
@CompileStatic
@TypeChecked
class AuthSettleAction implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        Balance balance = CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DELAY_DEBIT)
        Promise promise = context.orderServiceContext.billingFacade?.createBalance(balance)
        promise.then(new Promise.Func<Balance, Promise>() {
            @Override
            Promise apply(Balance b) {
                context.orderServiceContext.refreshBalances()
                return Promise.pure(b)
            }
        } ).syncThen { // TODO: update order status according to balance status.
            return null
        }
    }
}