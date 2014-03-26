package com.junbo.order.rest.resource

import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderService
import com.junbo.order.spec.model.ApiContext
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderResourceImpl)

    @Override
    Promise<Order> getOrderByOrderId(OrderId orderId) {
        return orderService.getOrderByOrderId(orderId.value)
    }

    @Override
    Promise<Order> createOrder(Order order) {
        assert (order != null && order.trackingUuid != null && order.user != null)
        def persistedOrder = orderService.getOrderByTrackingUuid(order.trackingUuid)
        if (persistedOrder != null) {
            LOGGER.info('name=Order_Already_Exist. userId:{}, trackingUuid: {}, orderId:{}',
                    order.user.value, order.trackingUuid, order.id.value)
            return Promise.pure(persistedOrder)
        }
        if (!order?.tentative) {
            return orderService.createOrder(order, new ApiContext(requestContext.headers))
        }
        return orderService.createQuote(order, new ApiContext(requestContext.headers))
    }

    @Override
    Promise<Order> updateOrderByOrderId(OrderId orderId, Order order) {
        assert (order != null && order.trackingUuid != null && order.user != null)
        def persistedOrder = orderService.getOrderByTrackingUuid(order.trackingUuid)
        if (persistedOrder != null) {
            LOGGER.info('name=Order_Already_Exist. userId:{}, trackingUuid: {}, orderId:{}',
                    order.user.value, order.trackingUuid, order.id.value)
            return Promise.pure(persistedOrder)
        }
        order.id = orderId
        orderService.getOrderByOrderId(orderId.value).then { Order oldOrder ->
            // handle the update request per scenario
            if (oldOrder.tentative) { // order not settle
                if (order.tentative) {
                    orderService.updateTentativeOrder(order,
                            new ApiContext(requestContext.headers)).syncThen { Order result ->
                        return result
                    }
                } else { // handle settle order scenario: the tentative flag is updated from true to false
                    orderService.settleQuote(oldOrder, new ApiContext(requestContext.headers))
                }
            } else { // order already settle
                Promise.pure(oldOrder) // todo implement update on settled order
            }
        }
    }

    @Override
    Promise<Results<Order>> getOrderByUserId(UserId userId) {
        orderService.getOrdersByUserId(userId.value).syncThen { List<Order> orders ->
            Results<Order> results = new Results<>()
            results.setItems(orders)
            return results
        }
    }
}
