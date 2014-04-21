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
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

/**
 * Charge Action for Web Payment.
 */
class WebPaymentChargeAction extends BaseOrderEventAwareAction {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder
    @Autowired
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(WebPaymentChargeAction)

    @Override
    @OrderEventAwareBefore(action = 'WebPaymentChargeAction')
    @OrderEventAwareAfter(action = 'WebPaymentChargeAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        orderInternalService.markSettlement(order)
        Promise promise =
                facadeContainer.billingFacade.createBalance(
                        CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.DEBIT))
        return promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_WebPaymentCharge_Error', throwable)
            throw AppErrors.INSTANCE.
                    billingConnectionError(CoreUtils.toAppErrors(throwable)).exception()
        }.then { Balance balance ->
            if (balance == null) {
                LOGGER.error('name=Order_WebPaymentCharge_Error_Balance_Null')
                throw AppErrors.INSTANCE.
                        billingConnectionError().exception()
            }
            if (balance.status != BalanceStatus.UNCONFIRMED.name()) {
                LOGGER.error('name=Order_WebPaymentCharge_Failed')
                throw AppErrors.INSTANCE.
                        billingChargeFailed().exception()
            }
            def billingEvent = BillingEventBuilder.buildBillingEvent(balance)
            orderRepository.createBillingEvent(order.id.value, billingEvent)
            orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context, billingEvent.status)
            }
        }
    }
}
