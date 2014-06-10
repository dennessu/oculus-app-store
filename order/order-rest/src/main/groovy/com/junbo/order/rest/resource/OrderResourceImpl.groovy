package com.junbo.order.rest.resource

import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderService
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

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
    OrderService orderService

    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderResourceImpl)

    @Override
    Promise<Order> getOrderByOrderId(OrderId orderId) {
        return orderService.getOrderByOrderId(orderId.value, true)
    }

    @Override
    Promise<Order> createOrder(Order order) {
        orderValidator.notNull(order, 'order').notNull(order.user, 'user')

        orderValidator.validateSettleOrderRequest(order)
        Boolean isTentative = order.tentative
        order.tentative = true
        return orderService.createQuote(order, new ApiContext()).then { Order ratedOrder ->
            if (!isTentative) {
                ratedOrder.tentative = isTentative
                return orderService.settleQuote(ratedOrder, new ApiContext())
            }
            return Promise.pure(ratedOrder)
        }
    }

    @Override
    Promise<Order> updateOrderByOrderId(OrderId orderId, Order order) {
        orderValidator.notNull(order, 'order').notNull(order.user, 'user')

        order.id = orderId

        return orderService.getOrderByOrderId(orderId.value, false).then { Order oldOrder ->
            // handle the update request per scenario
            if (oldOrder.tentative) { // order not settled
                if (order.tentative) {
                    // rate and update the tentative order
                    return orderService.updateTentativeOrder(order,
                            new ApiContext()).syncThen { Order result ->
                        return result
                    }
                } else { // handle settle order scenario: the tentative flag is updated from true to false
                    return orderService.settleQuote(order, new ApiContext())
                }
            } else { // order already settle
                // determine the refund request
                if(isARefund(oldOrder, order)) {
                    LOGGER.info('name=Refund_Non_Tentative_Offer')
                    oldOrder.orderItems = order.orderItems
                    return orderService.refundOrder(oldOrder)
                }
                LOGGER.info('name=Update_Non_Tentative_Offer')
                // update shipping address after settlement
                if (allowModification(oldOrder, order)) {
                    oldOrder.shippingAddress = order.shippingAddress
                    oldOrder.shippingToPhone = order.shippingToPhone
                    oldOrder.shippingToName = order.shippingToName
                    return orderService.updateNonTentativeOrder(oldOrder, new ApiContext())
                }
                LOGGER.error('name=Update_Not_Allow')
                throw AppErrors.INSTANCE.invalidSettledOrderUpdate().exception()
            }
        }
    }

    boolean allowModification(Order oldOrder, Order order) {
        // TODO: check the modification is allowed
        return (order.shippingAddress != null ? order.shippingAddress != oldOrder.shippingAddress : false) ||
                (order.shippingToName != null ? order.shippingToName != oldOrder.shippingToName : false) ||
                (order.shippingToPhone != null ? order.shippingToPhone != oldOrder.shippingToPhone : false)
    }

    boolean isARefund(Order olderOrder, Order newOrder) {
        assert (olderOrder != null)
        if (CollectionUtils.isEmpty(olderOrder.orderItems)) {
            throw AppErrors.INSTANCE.orderIsRefunded().exception()
        }

        if (CollectionUtils.isEmpty(newOrder.orderItems)) {
            return true
        }

        if(olderOrder.orderItems.size() > newOrder.orderItems.size()) {
            return true
        }

        Boolean isARefund = false
        newOrder.orderItems.each {OrderItem newItem ->
            OrderItem oldItem = olderOrder.orderItems.find {OrderItem oi ->
                newItem.offer.value == oi.offer.value
            }
            if(oldItem == null) {
                throw AppErrors.INSTANCE.orderItemIsNotFoundForRefund(newItem.offer.value.toString()).exception()
            }
            if(oldItem.quantity > newItem.quantity || oldItem.totalAmount > newItem.totalAmount) {
                isARefund = true
            }
        }
        return isARefund
    }

    @Override
    Promise<Results<Order>> getOrderByUserId(UserId userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        if (userId == null) {
            throw AppErrors.INSTANCE.missingParameterField('userId').exception()
        }
        return orderService.getOrdersByUserId(userId.value, orderQueryParam, pageParam).syncThen { List<Order> orders ->
            Results<Order> results = new Results<>()
            results.setItems(orders)
            return results
        }
    }
}
