package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceType
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.orderaction.context.BaseContext
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Auth Settle Action.
 */
@CompileStatic
@TypeChecked
class AuthSettleAction implements OrderAction<BaseContext> {

    @Override
    Promise<BaseContext> execute(BaseContext context) {
        Balance balance = CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DELAY_DEBIT)
        Promise promise = context.orderServiceContext.billingFacade?.createBalance(balance)
        promise.then(new Promise.Func<Balance, Promise>() {
            @Override
            Promise apply(Balance b) {
                if (context.orderServiceContext.balances == null) {
                    context.orderServiceContext.balances = []
                }
                context.orderServiceContext.balances.add(b)
                return Promise.pure(b)
            }
        } )

        // TODO: update order status according to balance status.
        return Promise.pure(context)
    }
}