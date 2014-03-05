package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.orderaction.context.BaseContext
import com.junbo.order.spec.model.BillingAction
import com.junbo.order.spec.model.EventStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
@TypeChecked
class ImmediateSettleAction implements OrderAction<BaseContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImmediateSettleAction)

    @Override
    Promise<BaseContext> execute(BaseContext context) {
        def order = context.orderServiceContext.order
        Balance balance = CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DEBIT)
        Promise promise = context.orderServiceContext.billingFacade?.createBalance(balance)
        return promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_ImmediateSettle_Error', throwable)
            return null
        }.syncThen { Balance resultBalance ->
            if (resultBalance == null) {
                // todo: log order charge action error?
                LOGGER.info('fail to create balance')
            } else {
                if (context.orderServiceContext.balances == null) {
                    context.orderServiceContext.balances = []
                }
                context.orderServiceContext.balances.add(resultBalance)
                context.orderServiceContext.orderRepository.saveBillingEvent(
                        order.id, balance.balanceId,
                        BillingAction.CHARGE, billingEventStatus)
                // TODO: update order status according to balance status.
            }
            return context
        }
    }

    private EventStatus getBillingEventStatus() {
        return null // todo: implement this
    }
}
