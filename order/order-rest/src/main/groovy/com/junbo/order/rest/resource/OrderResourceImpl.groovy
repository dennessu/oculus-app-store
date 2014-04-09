package com.junbo.order.rest.resource
import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderService
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.spec.model.ApiContext
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderQueryParam
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.container.ContainerRequestContext
//import javax.ws.rs.container.ContainerRequestContext
//import javax.ws.rs.core.Context
/**
 * Created by chriszhu on 2/10/14.
 */
@CompileStatic
@TypeChecked
@Scope('prototype')
@Component('defaultOrderResource')
class OrderResourceImpl implements OrderResource {

    @Autowired
    private ContainerRequestContext requestContext

    @Autowired
    OrderService orderService

    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderResourceImpl)

    @Override
    Promise<Order> getOrderByOrderId(OrderId orderId) {
        orderService.getOrderByOrderId(orderId.value).then { Order order ->
            if (order.tentative && CoreUtils.isRateExpired(order)) {
                // rate the order according to the honored until time
                return orderService.updateTentativeOrder(order, null)
            }
            return Promise.pure(order)
        }
    }

    @Override
    Promise<Order> createOrder(Order order) {
        orderValidator.notNull(order, 'order').notNull(order.trackingUuid, 'trackingUuid').notNull(order.user, 'user')

        def persistedOrder = orderService.getOrderByTrackingUuid(order.trackingUuid, order.user.value)
        if (persistedOrder != null) {
            LOGGER.info('name=Order_Same_TrackingUuid_Existed')
            return Promise.pure(persistedOrder)
        }

        orderValidator.validateSettleOrderRequest(order)
        Boolean isTentative = order.tentative
        order.tentative = true
        return orderService.createQuote(order, new ApiContext(requestContext.headers)).then { Order ratedOrder ->
            if (!isTentative) {
                ratedOrder.tentative = isTentative
                return orderService.settleQuote(ratedOrder, new ApiContext(requestContext.headers))
            }
            return Promise.pure(ratedOrder)
        }
    }

    @Override
    Promise<Order> updateOrderByOrderId(OrderId orderId, Order order) {
        orderValidator.notNull(order, 'order').notNull(order.user, 'user')

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
                LOGGER.info('name=Update_Non_Tentative_offer')
                Promise.pure(oldOrder) // todo implement update on settled order
            }
        }
    }

    @Override
    Promise<Results<Order>> getOrderByUserId(UserId userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        orderService.getOrdersByUserId(userId.value, orderQueryParam, pageParam).syncThen { List<Order> orders ->
            Results<Order> results = new Results<>()
            results.setItems(orders)
            return results
        }
    }
}
