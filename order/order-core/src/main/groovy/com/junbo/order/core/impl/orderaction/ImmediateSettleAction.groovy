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
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.model.enums.BillingAction
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
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
@TypeChecked
class ImmediateSettleAction extends BaseOrderEventAwareAction {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepositoryFacade orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder
    @Autowired
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(ImmediateSettleAction)

    @Override
    @OrderEventAwareBefore(action = 'ImmediateSettleAction')
    @OrderEventAwareAfter(action = 'ImmediateSettleAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        orderInternalService.markSettlement(order)
        CoreUtils.readHeader(order, context?.orderServiceContext?.apiContext)
        Promise promise =
                facadeContainer.billingFacade.createBalance(
                        CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.DEBIT),
                        context?.orderServiceContext?.apiContext?.asyncCharge)
        return promise.then { Balance balance ->
            assert(balance != null)
            if (balance.status != BalanceStatus.AWAITING_PAYMENT.name() &&
                    balance.status != BalanceStatus.COMPLETED.name() &&
                    balance.status != BalanceStatus.QUEUING.name()) {
                LOGGER.error('name=Order_ImmediateSettle_Failed')
                throw AppErrors.INSTANCE.billingChargeFailed().exception()
            }

            context.orderServiceContext.isAsyncCharge = balance.isAsyncCharge
            CoreBuilder.fillTaxInfo(order, balance)
            orderInternalService.persistBillingHistory(balance, BillingAction.CHARGE, order)
            return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        BillingEventHistoryBuilder.buildEventStatusFromBalance(balance))
            }
        }
    }
}
