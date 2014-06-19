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
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.enums.BillingAction
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
/**
 * Charge Action for Web Payment.
 */
@CompileStatic
class WebPaymentChargeAction extends BaseOrderEventAwareAction {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepositoryFacade orderRepository
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
        if (order.successRedirectUrl == null) {
            throw AppErrors.INSTANCE.missingParameterField('successRedirectUrl').exception()
        }
        if (order.cancelRedirectUrl == null) {
            throw AppErrors.INSTANCE.missingParameterField('cancelRedirectUrl').exception()
        }
        Promise promise =
                facadeContainer.billingFacade.createBalance(
                        CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.DEBIT),
                        context?.orderServiceContext?.apiContext?.asyncCharge)
        return promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_WebPaymentCharge_Error', throwable)
            throw facadeContainer.billingFacade.convertError(throwable).exception()
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
            if (balance.providerConfirmUrl == null) {
                LOGGER.error('name=Order_WebPaymentCharge_Empty_ProviderConfirmUrl')
                throw AppErrors.INSTANCE.billingChargeFailed().exception()
            }
            order.providerConfirmUrl = balance.providerConfirmUrl
            def oldOrder = orderRepository.getOrder(order.getId().value)
            oldOrder.providerConfirmUrl = order.providerConfirmUrl
            orderRepository.updateOrder(oldOrder, true, false, null)
            CoreBuilder.fillTaxInfo(order, balance)
            orderInternalService.persistBillingHistory(balance, BillingAction.PENDING_CHARGE, order)
            return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        BillingEventHistoryBuilder.buildEventStatusFromBalance(balance))
            }
        }
    }
}
