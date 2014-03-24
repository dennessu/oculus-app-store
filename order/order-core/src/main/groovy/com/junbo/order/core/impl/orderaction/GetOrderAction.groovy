package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.OrderStatusBuilder
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Created by LinYi on 14-2-21.
 */
@CompileStatic
class GetOrderAction implements Action {
    @Resource(name = 'orderRepository')
    OrderRepository orderRepository
    @Resource(name = 'orderStatusBuilder')
    OrderStatusBuilder orderStatusBuilder

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        Long orderId = (Long)actionContext.requestScope['GetOrderAction_OrderId']
        Long userId = (Long)actionContext.requestScope['GetOrderAction_UserId']
        if (orderId != null) {
            // get Order by id
            def order = orderRepository.getOrder(orderId)
            refreshOrderStatus(order)
            context.orderServiceContext.order = order
        }
        else if (userId != null) {
            // get order by userId
            def orders = orderRepository.getOrdersByUserId(userId)
            orders.each { Order order ->
                refreshOrderStatus(order)
            }
            context.orderServiceContext.orders = orders
        }
        return Promise.pure(null)
    }

    private void refreshOrderStatus(Order order) {
        def status = order.status = OrderStatusBuilder.buildOrderStatus(order,
                orderRepository.getOrderEvents(order.id.value))
        if (status != order.status) {
           order.status = status
            orderRepository.updateOrder(order, true)
        }
    }
}
