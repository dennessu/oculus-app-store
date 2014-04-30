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
import com.junbo.order.core.impl.common.BillingEventBuilder
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.payment.spec.model.PaymentInstrument
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
    @Autowired
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(PhysicalSettleAction)

    boolean completeCharge

    @Override
    @OrderEventAwareBefore(action = 'PhysicalSettleAction')
    @OrderEventAwareAfter(action = 'PhysicalSettleAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        if (completeCharge) {
            return orderServiceContextBuilder.getPaymentInstruments(context.orderServiceContext)
                    .then { List<PaymentInstrument> pis ->
                if (PIType.get(pis[0].type) == PIType.PAYPAL) {
                    // for web payment, balance is already fully charged at web payment flow
                    return Promise.pure(actionContext)
                }
                // complete charge, update the balance to the remaining amount
                return facadeContainer.billingFacade.quoteBalance(
                        CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.DEBIT)).syncRecover {
                    Throwable throwable ->
                        LOGGER.error('name=Fail_To_Quote_Balance', throwable)
                        throw AppErrors.INSTANCE.billingConnectionError().exception()
                }.then { Balance taxedBalance ->
                    if (taxedBalance == null) {
                        LOGGER.info('name=Fail_To_Calculate_Tax_Balance_Not_Found')
                        throw AppErrors.INSTANCE.balanceNotFound().exception()
                    }
                    Balance balance = CoreBuilder.buildPartialChargeBalance(context.orderServiceContext.order,
                            BalanceType.DEBIT, taxedBalance)
                    // post balance with tax info
                    return facadeContainer.billingFacade.createBalance(balance).syncRecover { Throwable throwable ->
                        LOGGER.error('name=Order_PhysicalSettle_CompleteCharge_Error', throwable)
                        // TODO: retry/refund when failing to charge the remaining amount
                        throw facadeContainer.billingFacade.convertError(throwable).exception()
                    }.then { Balance resultBalance ->
                        if (resultBalance == null) {
                            LOGGER.error('name=Order_PhysicalSettle_CompleteCharge_Error_Balance_Null')
                            throw AppErrors.INSTANCE.billingConnectionError().exception()
                        }
                        def billingEvent = BillingEventBuilder.buildBillingEvent(resultBalance)
                        orderRepository.createBillingEvent(context.orderServiceContext.order.id.value, billingEvent)
                        orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                            return CoreBuilder.buildActionResultForOrderEventAwareAction(context, billingEvent.status)
                        }
                    }
                }
            }
        }
        orderInternalService.markSettlement(context.orderServiceContext.order)
        // partial charge, post a 50$ balance
        Balance balance = CoreBuilder.buildPartialChargeBalance(context.orderServiceContext.order,
                BalanceType.DEBIT, null)
        Promise promise = facadeContainer.billingFacade.createBalance(balance)
        promise.syncRecover {  Throwable throwable ->
            LOGGER.error('name=Order_PhysicalSettle_Error', throwable)
            context.orderServiceContext.order.tentative = true
            throw facadeContainer.billingFacade.convertError(throwable).exception()
        }.then { Balance resultBalance ->
            if (resultBalance == null) {
                LOGGER.error('name=Order_PhysicalSettle_Error_Balance_Null')
                throw AppErrors.INSTANCE.
                        billingConnectionError().exception()
            }
            def billingEvent = BillingEventBuilder.buildBillingEvent(resultBalance)
            orderRepository.createBillingEvent(context.orderServiceContext.order.id.value, billingEvent)
            orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context, billingEvent.status)
            }
        }

    }
}
