package com.junbo.order.rest.resource

import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderService
import com.junbo.order.spec.model.ApiContext
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.container.ContainerRequestContext

//import javax.ws.rs.container.ContainerRequestContext
//import javax.ws.rs.core.Context
/**
 * Created by chriszhu on 2/10/14.
 */
@CompileStatic
@Scope('prototype')
@Component('defaultOrderResource')
class OrderResourceImpl implements OrderResource {

    @Autowired
    private ContainerRequestContext requestContext

    @Autowired
    OrderService orderService

    @Override
    Promise<Order> getOrderByOrderId(OrderId orderId) {
        return orderService.getOrderByOrderId(orderId.value)
    }

    @Override
    Promise<List<Order>> createOrders(Order order) {
        if (!order?.tentative) {
            return orderService.createOrders(order, new ApiContext(requestContext.headers))
        }

        return orderService.createQuotes(order, new ApiContext(requestContext.headers))

    }

    @Override
    Promise<List<Order>> updateOrderByOrderId(OrderId orderId, Order order) {
        order.id = orderId
        orderService.getOrderByOrderId(orderId.value).then { Order oldOrder ->
            // handle the update request per scenario
            if (oldOrder.tentative) { // order not settle
                if (order.tentative) {
                    orderService.updateTentativeOrder(order,
                            new ApiContext(requestContext.headers)).syncThen { Order result ->
                        [result]
                    }
                } else { // handle settle order scenario: the tentative flag is updated from true to false
                    orderService.settleQuote(oldOrder, new ApiContext(requestContext.headers))
                }
            } else { // order already settle
                Promise.pure([oldOrder]) // todo implement update on settled order
            }
        }
    }

    @Override
    Promise<List<Order>> getOrderByUserId(UserId userId) {
        return orderService.getOrdersByUserId(userId.value)
    }

}
