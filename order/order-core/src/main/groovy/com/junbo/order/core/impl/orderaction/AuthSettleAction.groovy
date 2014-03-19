package com.junbo.order.core.impl.orderaction
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.OrderStatusBuilder
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Auth Settle Action.
 */
@CompileStatic
@TypeChecked
class AuthSettleAction implements Action {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        Balance balance = CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DELAY_DEBIT)
        def order = context.orderServiceContext.order
        Promise promise = facadeContainer.billingFacade.createBalance(balance)
        promise.syncRecover {
            orderRepository.createOrderEvent(
                    CoreBuilder.buildOrderEvent(
                            order.id,
                            OrderActionType.AUTHORIZE,
                            EventStatus.ERROR,
                            ActionUtils.getFlowType(actionContext),
                            context.trackingUuid))
            return null
        }.then {
            orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                // TODO: update order status according to balance status.
                // Update order status according to balance status.
                // TODO get order events to update the order status
                def o = orderRepository.getOrder(context.orderServiceContext.order.id.value)
                o.status = OrderStatusBuilder.buildOrderStatusFromBalance(balance.status).toString()
                orderRepository.updateOrder(o, true)
                // TODO: save order level tax
                orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(
                                order.id,
                                OrderActionType.AUTHORIZE,
                                CoreBuilder.buildEventStatusFromBalance(balance.status),
                                ActionUtils.getFlowType(actionContext),
                                context.trackingUuid))
                return null
            }
        }
    }
}