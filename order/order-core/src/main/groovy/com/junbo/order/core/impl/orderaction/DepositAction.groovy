package com.junbo.order.core.impl.orderaction

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
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.enums.BillingAction
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

/**
 * Charge deposit action for Preorder.
 */
@CompileStatic
@TypeChecked
class DepositAction extends BaseOrderEventAwareAction {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepositoryFacade orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder
    @Autowired
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositAction)

    @Override
    @OrderEventAwareBefore(action = 'DepositAction')
    @OrderEventAwareAfter(action = 'DepositAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        CoreUtils.readHeader(order, context?.orderServiceContext?.apiContext)
        orderInternalService.markSettlement(context.orderServiceContext.order)
        Balance balance = CoreBuilder.buildPartialChargeBalance(context.orderServiceContext.order,
                BalanceType.DEBIT, null)
        Promise promise = facadeContainer.billingFacade.createBalance(balance,
                context?.orderServiceContext?.apiContext?.asyncCharge)
        return promise.syncRecover {  Throwable throwable ->
            LOGGER.error('name=Order_PhysicalSettle_Error', throwable)
            context.orderServiceContext.order.tentative = true
            throw facadeContainer.billingFacade.convertError(throwable).exception()
        }.then { Balance resultBalance ->
            if (resultBalance == null) {
                LOGGER.error('name=Order_PhysicalSettle_Error_Balance_Null')
                throw AppErrors.INSTANCE.
                        billingConnectionError().exception()
            }
            context.orderServiceContext.isAsyncCharge = resultBalance.isAsyncCharge
            orderInternalService.persistBillingHistory(balance, BillingAction.DEPOSIT, order)
            return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        BillingEventHistoryBuilder.buildEventStatusFromBalance(resultBalance))
            }
        }

    }
}
