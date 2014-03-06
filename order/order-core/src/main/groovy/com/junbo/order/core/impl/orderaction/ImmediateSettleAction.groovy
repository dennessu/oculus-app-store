package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
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
class ImmediateSettleAction implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImmediateSettleAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        def balance = CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DEBIT)
        Promise promise = context.orderServiceContext.billingFacade?.createBalance(balance)
        return promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_ImmediateSettle_Error', throwable)
            return null
        }.syncThen { Balance resultBalance ->
            if (resultBalance == null) {
                // todo: log order charge action error?
                LOGGER.info('fail to create balance')
            } else {
                context.orderServiceContext.orderRepository.saveBillingEvent(
                        order.id, balance.balanceId,
                        BillingAction.CHARGE, billingEventStatus)
                context.orderServiceContext.refreshBalances()
                // TODO: update order status according to balance status.
            }
            return null
        }
    }

    private EventStatus getBillingEventStatus() {
        return null // todo: implement this
    }
}
