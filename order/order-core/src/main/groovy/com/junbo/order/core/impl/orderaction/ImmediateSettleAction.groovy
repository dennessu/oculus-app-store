package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.OrderStatusBuilder
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.BillingAction
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.BillingEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

/**
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
@TypeChecked
class ImmediateSettleAction extends BaseOrderEventAwareAction {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    private static final Logger LOGGER = LoggerFactory.getLogger(ImmediateSettleAction)

    @Override
    @OrderEventAwareBefore(action = 'ImmediateSettleAction')
    @OrderEventAwareAfter(action = 'ImmediateSettleAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        Promise promise =
                facadeContainer.billingFacade.createBalance(
                        CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DEBIT))
        return promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_ImmediateSettle_Error', throwable)
            throw AppErrors.INSTANCE.billingConnectionError().exception()
        }.syncThen { Balance balance ->
            if (balance == null) {
                // todo: log order charge action error?
                LOGGER.info('fail to create balance')
                CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.ERROR)
                throw AppErrors.INSTANCE.billingConnectionError().exception()
            }
            def billingEvent = new BillingEvent()
            billingEvent.balanceId = (balance.balanceId == null || balance.balanceId.value == null) ?
                    null : balance.balanceId.value.toString()
            billingEvent.action = BillingAction.CHARGE.name()
            billingEvent.status = OrderStatusBuilder.buildEventStatusFromBalance(balance.status)
            orderRepository.createBillingEvent(order.id.value, billingEvent)
            orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        CoreBuilder.buildEventStatusFromBalance(balance.status))
            }
        }
    }
}
