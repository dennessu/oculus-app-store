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

import javax.ws.rs.core.HttpHeaders
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
    OrderService orderService

    @Override
    Promise<Order> getOrderByOrderId(OrderId orderId, HttpHeaders httpHeaders) {
        return orderService.getOrderByOrderId(orderId.value)
    }

    @Override
    Promise<List<Order>> createOrders(Order order, HttpHeaders httpHeaders) {
        if (!order?.tentative) {
            return orderService.createOrders(order, new ApiContext(httpHeaders))
        }

        return orderService.createQuotes(order, new ApiContext(httpHeaders))

    }

    @Override
    Promise<List<Order>> updateOrderByOrderId(OrderId orderId, Order order, HttpHeaders httpHeaders) {
        orderService.getOrderByOrderId(orderId.value).syncThen { Order o ->
            // handle the update request per scenario
            // handle settle order scenario: the tentative flag is updated from false to true
            if (o.tentative == false && order.tentative == true) {
                // The order need to be settled regardless of the other updates
                orderService.settleQuote(order, new ApiContext(httpHeaders))
            } else if (o.tentative == false) {
                orderService.updateTentativeOrder(order, new ApiContext(httpHeaders))
            }

        }
    }

    @Override
    Promise<List<Order>> getOrderByUserId(UserId userId, HttpHeaders headers) {
        return null
    }

}
