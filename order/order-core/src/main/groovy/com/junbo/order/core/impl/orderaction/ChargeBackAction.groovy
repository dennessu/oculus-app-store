package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderStatus
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

        order.setStatus(OrderStatus.CHARGE_BACK.name())
        return facadeContainer.billingFacade.getBalancesByOrderId(order.getId().value).then { List<Balance> balances ->
            def bs = []
            for (Balance balance : balances) {
                if (balance.status == BalanceStatus.COMPLETED || balance.status == BalanceStatus.AWAITING_PAYMENT) {
                    balance.status = BalanceStatus.CHARGE_BACK.name()
                    bs.add(balance)
                }
            }
            Promise.each(bs).then { Balance b ->
                return facadeContainer.billingFacade.putBalance(b).recover { Throwable throwable ->
                    LOGGER.error('name=ChargeBakce_PutBalance_Error')
                    throw throwable
                }
            }.then {
                LOGGER.info('name=ChargeBakce_PutBalance_Complete')
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.COMPLETED)
            }
        }

    }
}
