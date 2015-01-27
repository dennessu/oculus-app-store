package com.junbo.order.core.impl.orderaction
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.BillingEventHistoryBuilder
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.EventStatus
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
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        if (order.tentative) {
            throw AppErrors.INSTANCE.orderAlreadyInSettleProcess().exception()
        }
        CoreUtils.readHeader(order, context?.orderServiceContext?.apiContext)
        String actionResultStr = null
        return Promise.pure().then {
            return facadeContainer.billingFacade.createBalance(CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.DEBIT),
                    context?.orderServiceContext?.apiContext?.asyncCharge)
        }.recover { Throwable ex ->
            LOGGER.error('name=Order_Unexpected_Billing_Error_ImmediateSettle', ex)
            return Promise.pure(null)
        }.then { Balance balance ->
            if (balance == null) {
                LOGGER.error('name=Order_Unexpected_Billing_Error_ImmediateSettle')
                actionResultStr = 'ERROR'
                orderInternalService.markErrorStatus(order)
                return Promise.pure(CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        EventStatus.FAILED, actionResultStr))
            }
            if (balance.status == BalanceStatus.FAILED.name()) {
                // declined. indicate to rollback the tentative flag
                LOGGER.info('name=Order_ImmediateSettle_Declined. orderId: {}', order.getId().value)
                actionResultStr = 'ROLLBACK'
                orderInternalService.markTentative(order)
            } else if (balance.status != BalanceStatus.AWAITING_PAYMENT.name() &&
                    balance.status != BalanceStatus.COMPLETED.name() &&
                    balance.status != BalanceStatus.QUEUING.name()) {
                LOGGER.error('name=Order_ImmediateSettle_UnexpectedStatus.BalanceStatus:' + balance.status)
                actionResultStr = 'ERROR'
                orderInternalService.markErrorStatus(order)
            } else {
                LOGGER.error('name=Order_ImmediateSettle_Success. BalanceStatus:' + balance.status)
                actionResultStr = 'SUCCESS'
            }
            context.orderServiceContext.isAsyncCharge = balance.isAsyncCharge
            CoreBuilder.fillTaxInfo(order, balance)

            orderInternalService.persistBillingHistory(balance, BillingAction.CHARGE, order)
            return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        BillingEventHistoryBuilder.buildEventStatusFromBalance(balance), actionResultStr)
            }
        }.recover { Throwable ex ->
            // catch any exception during processing the balance result
            LOGGER.error('name=Order_Unexpected_Error_ImmediateSettle', ex)
            actionResultStr = 'ERROR'
            orderInternalService.markErrorStatus(order)
            return Promise.pure(CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                    EventStatus.FAILED, actionResultStr))
        }
    }
}
