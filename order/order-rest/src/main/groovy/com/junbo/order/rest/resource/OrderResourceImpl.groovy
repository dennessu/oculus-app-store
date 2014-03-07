package com.junbo.order.rest.resource

import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderService
import com.junbo.order.spec.model.*
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

//import javax.ws.rs.container.ContainerRequestContext
//import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
/**
 * Created by chriszhu on 2/10/14.
 */
@CompileStatic
@Scope('prototype')
@Component('defaultOrderResource')
class OrderResourceImpl implements OrderResource {

    @Autowired
    OrderService orderService

//    @Context
//    private ContainerRequestContext requestContext
//
//    @Context
//    private RespondingContext respondingContext

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(OrderId orderId, HttpHeaders httpHeaders) {
        return orderService.getOrderByOrderId(orderId.getValue())
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
        orderService.getOrderByOrderId(orderId.getValue()).syncThen { Order o ->
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
    Promise<List<OrderEvent>> getOrderEvents(OrderId orderId, HttpHeaders httpHeaders) {
        def orderEvents = []
        orderEvents.add(new OrderEvent())
        return Promise.pure(orderEvents)
    }

    @Override
    Promise<List<Discount>> getOrderDiscounts(OrderId orderId, HttpHeaders httpHeaders) {
        def discounts = []
        discounts.add(new Discount())
        return Promise.pure(discounts)
    }

    @Override
    Promise<List<OrderItem>> getOrderItemsByOrderId(OrderId orderId, HttpHeaders headers) {
        return null
    }

    @Override
    Promise<OrderEvent> createOrderEvent(OrderId orderId, HttpHeaders headers) {
        return null
    }
}
