package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.EventStatus
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthSettleAction)

    @Override
    @OrderEventAwareBefore(action = 'AuthSettleAction')
    @OrderEventAwareAfter(action = 'AuthSettleAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        Balance balance = CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DELAY_DEBIT)
        Promise promise = facadeContainer.billingFacade.createBalance(balance)
        promise.syncRecover {  Throwable throwable ->
            LOGGER.error('name=Order_AuthSettle_Error', throwable)
            return null
        }.then { Balance it ->
            if (it == null) {
                CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.ERROR)
                throw AppErrors.INSTANCE.billingConnectionError().exception()
            }
            orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: save order level tax
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context,
                        CoreBuilder.buildEventStatusFromBalance(balance.status))
            }
        }
    }
}