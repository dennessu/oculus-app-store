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
import com.junbo.order.db.entity.enums.BillingAction
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.BillingEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
@TypeChecked
class ImmediateSettleAction implements Action {
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository
    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    private static final Logger LOGGER = LoggerFactory.getLogger(ImmediateSettleAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        Promise promise =
                facadeContainer.billingFacade.createBalance(
                        CoreBuilder.buildBalance(context.orderServiceContext, BalanceType.DEBIT))
        return promise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_ImmediateSettle_Error', throwable)
            orderRepository.createOrderEvent(
                    CoreBuilder.buildOrderEvent(
                            order.id,
                            OrderActionType.CHARGE,
                            EventStatus.ERROR,
                            ActionUtils.getFlowType(actionContext),
                            context.trackingUuid))
        }.syncThen { Balance balance ->
            if (balance == null) {
                // todo: log order charge action error?
                LOGGER.info('fail to create balance')
                orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(
                                order.id,
                                OrderActionType.CHARGE,
                                EventStatus.ERROR,
                                ActionUtils.getFlowType(actionContext),
                                context.trackingUuid))
            } else {
                def billingEvent = new BillingEvent()
                billingEvent.balanceId = (balance.balanceId == null || balance.balanceId.value == null) ?
                        null : balance.balanceId.value.toString()
                billingEvent.action = BillingAction.CHARGE.name()
                billingEvent.status = billingEventStatus.name()
                orderRepository.createBillingEvent(order.id.value, billingEvent)
                orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                    return null
                }
                // Update order status according to balance status.
                // TODO get order events to update the order status
                def o = orderRepository.getOrder(order.id.value)
                o.status = OrderStatusBuilder.buildOrderStatusFromBalance(balance.status).toString()
                orderRepository.updateOrder(o, true)
                // TODO: save order level tax
                orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(
                                order.id,
                                OrderActionType.CHARGE,
                                CoreBuilder.buildEventStatusFromBalance(balance.status),
                                ActionUtils.getFlowType(actionContext),
                                context.trackingUuid))
            }
            return null
        }
    }

    private EventStatus getBillingEventStatus() {
        return EventStatus.OPEN // todo: implement this
    }
}
