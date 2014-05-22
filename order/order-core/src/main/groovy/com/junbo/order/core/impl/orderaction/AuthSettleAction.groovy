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
import com.junbo.order.core.impl.common.BillingEventHistoryBuilder
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.BillingAction
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
/**
 * Auth Settle Action.
 */
@CompileStatic
@TypeChecked
class AuthSettleAction extends BaseOrderEventAwareAction {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepositoryFacade orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder
    @Autowired
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthSettleAction)

    @Override
    @OrderEventAwareBefore(action = 'AuthSettleAction')
    @OrderEventAwareAfter(action = 'AuthSettleAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        orderInternalService.markSettlement(order)
        Balance balance = CoreBuilder.buildBalance(order, BalanceType.MANUAL_CAPTURE)
        Promise promise = facadeContainer.billingFacade.createBalance(balance,
                context?.orderServiceContext?.apiContext?.asyncCharge)
        return promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_AuthSettle_Error', throwable)
            context.orderServiceContext.order.tentative = true
            throw facadeContainer.billingFacade.convertError(throwable).exception()
        }.then { Balance resultBalance ->
            context.orderServiceContext.order.tentative = true
            if (resultBalance == null) {
                LOGGER.error('name=Order_AuthSettle_Error_Balance_Null')
                throw AppErrors.INSTANCE.
                        billingConnectionError().exception()
            }
            if (resultBalance.status != BalanceStatus.PENDING_CAPTURE.name()) {
                LOGGER.error('name=Order_AuthSettle_Failed')
                throw AppErrors.INSTANCE.
                        billingChargeFailed().exception()
            }
            context.orderServiceContext.order.tentative = false
            CoreBuilder.fillTaxInfo(order, resultBalance)
            def billingHistory = BillingEventHistoryBuilder.buildBillingHistory(resultBalance)
            if (billingHistory.billingEvent != null) {
                if (billingHistory.billingEvent == BillingAction.AUTHORIZE.name()) {
                    order.payments?.get(0)?.paymentAmount = billingHistory.totalAmount
                }
                def savedHistory = orderRepository.createBillingHistory(order.getId().value, billingHistory)
                if (order.billingHistories == null) {
                    order.billingHistories = [savedHistory]
                }
                else {
                    order.billingHistories.add(savedHistory)
                }
            }
            return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        BillingEventHistoryBuilder.buildEventStatusFromBalance(resultBalance))
            }
        }
    }
}