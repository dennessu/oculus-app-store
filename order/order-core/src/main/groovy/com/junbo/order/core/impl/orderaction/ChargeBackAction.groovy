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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Created by xmchen on 15/2/9.
 */
class ChargeBackAction extends BaseOrderEventAwareAction {
    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeBackAction)

    @Override
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        CoreUtils.readHeader(order, context?.orderServiceContext?.apiContext)

        Balance b = CoreBuilder.buildBalance(order, BalanceType.CHARGE_BACK, true)
        return facadeContainer.billingFacade.createBalance(b, false).syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_ChargeBack_Balance_Error', throwable)
            throw facadeContainer.billingFacade.convertError(throwable).exception()
        }.then { Balance balance ->
            if (balance == null) {
                LOGGER.error('name=Order_ChargeBack_Balance_Error_Balance_Null')
                throw AppErrors.INSTANCE.billingConnectionError().exception()
            }
            orderInternalService.persistBillingHistory(balance, BillingAction.CHARGE_BACK, order)
            return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        BillingEventHistoryBuilder.buildEventStatusFromBalance(balance))
            }
        }
    }
}
