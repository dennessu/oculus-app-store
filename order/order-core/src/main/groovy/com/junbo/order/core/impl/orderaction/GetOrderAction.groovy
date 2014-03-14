package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.db.repo.OrderRepository
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by LinYi on 14-2-21.
 */
class GetOrderAction implements Action {
    @Autowired
    OrderRepository orderRepository

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        Long orderId = (Long)actionContext.requestScope['GetOrderAction_OrderId']
        Long userId = (Long)actionContext.requestScope['GetOrderAction_UserId']
        if (orderId != null) {
            // get Order by id
            def order = orderRepository.getOrder(orderId)
            context.orderServiceContext.order = order
        }
        else if (userId != null) {
            // get order by userId
            def orders = orderRepository.getOrdersByUserId(userId)
            context.orderServiceContext.orders = orders
        }
        return Promise.pure(null)
    }
}
