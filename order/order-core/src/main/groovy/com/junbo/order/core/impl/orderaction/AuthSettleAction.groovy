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
    OrderRepository orderRepository
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
        orderInternalService.markSettlement(context.orderServiceContext.order)
        Balance balance = CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.CREDIT)
        Promise promise = facadeContainer.billingFacade.createBalance(balance)
        promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_AuthSettle_Error', throwable)
            context.orderServiceContext.order.tentative = true
            throw AppErrors.INSTANCE.
                    billingConnectionError(CoreUtils.toAppErrors(throwable)).exception()
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
            def billingEvent = BillingEventBuilder.buildBillingEvent(resultBalance)
            orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context, billingEvent.status)
            }
        }
    }
}