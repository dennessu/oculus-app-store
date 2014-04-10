package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.common.BillingEventBuilder
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

/**
 * Settle Action for Physical Goods.
 */
@CompileStatic
@TypeChecked
class PhysicalSettleAction extends BaseOrderEventAwareAction {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    private static final Logger LOGGER = LoggerFactory.getLogger(PhysicalSettleAction)

    @Override
    @OrderEventAwareBefore(action = 'PhysicalSettleAction')
    @OrderEventAwareAfter(action = 'PhysicalSettleAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        Balance balance = CoreBuilder.buildPartialChargeBalance(context.orderServiceContext.order, BalanceType.DEBIT)
        Promise promise = facadeContainer.billingFacade.createBalance(balance)
        promise.syncRecover {  Throwable throwable ->
            LOGGER.error('name=Order_PhysicalSettle_Error', throwable)
            context.orderServiceContext.order.tentative = true
            throw AppErrors.INSTANCE.
                    billingConnectionError(CoreUtils.toAppErrors(throwable)).exception()
        }.then { Balance resultBalance ->
            if (resultBalance == null) {
                LOGGER.error('name=Order_PhysicalSettle_Error_Balance_Null')
                throw AppErrors.INSTANCE.
                        billingConnectionError().exception()
            }
            if (resultBalance.status != BalanceStatus.AWAITING_PAYMENT.name()) {
                LOGGER.error('name=Order_PhysicalSettle_Failed')
                throw AppErrors.INSTANCE.
                        billingChargeFailed().exception()
            }
            def billingEvent = BillingEventBuilder.buildBillingEvent(resultBalance)
            orderRepository.createBillingEvent(context.orderServiceContext.order.id.value, billingEvent)
            orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context, billingEvent.status)
            }
        }
    }
}
