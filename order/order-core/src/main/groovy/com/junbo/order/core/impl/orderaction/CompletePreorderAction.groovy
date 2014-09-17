package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.PIType
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
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Complete Preorder action.
 */
@CompileStatic
@TypeChecked
class CompletePreorderAction extends BaseOrderEventAwareAction {
    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletePreorderAction)


    @Override
    @OrderEventAwareBefore(action = 'CompletePreorderAction')
    @OrderEventAwareAfter(action = 'CompletePreorderAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        CoreUtils.readHeader(order, context?.orderServiceContext?.apiContext)
        /* physical goods fulfilled
        1. preorder with CC: charge the remaining amount of preorder
        2. preorder with paypal: balance is already fully charged, skip this action
         */
        return orderServiceContextBuilder.getPaymentInstruments(context.orderServiceContext)
                .then { List<PaymentInstrument> pis ->
            if (PIType.get(pis[0].type) == PIType.PAYPAL || PIType.get(pis[0].type) == PIType.OTHERS) {
                return Promise.pure(actionContext)
            }
            // complete charge, update the balance to the remaining amount
            return facadeContainer.billingFacade.quoteBalance(
                    CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.DEBIT)).syncRecover {
                Throwable throwable ->
                    LOGGER.error('name=Fail_To_Quote_Balance', throwable)
                    throw throwable
            }.then { Balance taxedBalance ->
                if (taxedBalance == null) {
                    LOGGER.info('name=Fail_To_Calculate_Tax_Balance_Not_Found')
                    throw AppErrors.INSTANCE.balanceNotFound().exception()
                }
                Balance balance = CoreBuilder.buildPartialChargeBalance(context.orderServiceContext.order,
                        BalanceType.DEBIT, taxedBalance)
                // post balance with tax info
                return facadeContainer.billingFacade.createBalance(balance,
                        context?.orderServiceContext?.apiContext?.asyncCharge).syncRecover { Throwable throwable ->
                    LOGGER.error('name=Order_PhysicalSettle_CompleteCharge_Error', throwable)
                    // TODO: retry/refund when failing to charge the remaining amount
                    throw facadeContainer.billingFacade.convertError(throwable).exception()
                }.then { Balance resultBalance ->
                    if (resultBalance == null) {
                        LOGGER.error('name=Order_CompletePreorder_Error_Balance_Null')
                        throw AppErrors.INSTANCE.billingConnectionError().exception()
                    }
                    context.orderServiceContext.isAsyncCharge = resultBalance.isAsyncCharge
                    orderInternalService.persistBillingHistory(balance, BillingAction.CHARGE, order)
                    return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                        return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                                BillingEventHistoryBuilder.buildEventStatusFromBalance(resultBalance))
                    }
                }
            }
        }

    }
}
