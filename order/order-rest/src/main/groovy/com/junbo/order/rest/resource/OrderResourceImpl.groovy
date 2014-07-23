package com.junbo.order.rest.resource

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.auth.OrderAuthorizeCallbackFactory
import com.junbo.order.core.OrderService
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.resource.OrderResource
import com.junbo.csr.spec.def.CsrLogActionType
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
@Component('defaultOrderResource')
class OrderResourceImpl implements OrderResource {

    @Autowired
    OrderService orderService

    @Qualifier('order.csrLogClient')
    @Autowired
    CsrLogResource csrLogResource

    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator

    @Autowired
    AuthorizeService authorizeService

    @Autowired
    OrderAuthorizeCallbackFactory authorizeCallbackFactory

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderResourceImpl)

    @Override
    Promise<Order> getOrderByOrderId(OrderId orderId) {
        return orderService.getOrderByOrderId(orderId.value, true, new OrderServiceContext(), false).then { Order order ->
            def callback = authorizeCallbackFactory.create(order)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.orderNotFound().exception()
                }

                return Promise.pure(order)
            }
        }
    }

    @Override
    Promise<Order> createOrder(Order order) {
        orderValidator.notNull(order, 'order').notNull(order.user, 'user')

        def callback = authorizeCallbackFactory.create(order)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            orderValidator.validateSettleOrderRequest(order)
            Boolean isTentative = order.tentative
            order.tentative = true
            return orderService.createQuote(order,
                    new OrderServiceContext(order, new ApiContext())).then { Order ratedOrder ->
                if (!isTentative) {
                    ratedOrder.tentative = isTentative
                    return orderService.settleQuote(ratedOrder, new OrderServiceContext(order, new ApiContext()))
                }
                return Promise.pure(ratedOrder)
            }
        }
    }

    @Override
    Promise<Order> updateOrderByOrderId(OrderId orderId, Order order) {
        orderValidator.notNull(order, 'order').notNull(order.user, 'user')

        def callback = authorizeCallbackFactory.create(order)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            order.id = orderId

            return orderService.getOrderByOrderId(orderId.value, false, new OrderServiceContext(), false).then { Order oldOrder ->
                // handle the update request per scenario
                if (oldOrder.tentative) { // order not settled
                    if (order.tentative) {
                        // rate and update the tentative order
                        return orderService.updateTentativeOrder(order,
                                new OrderServiceContext(order, new ApiContext())).syncThen { Order result ->
                            return result
                        }
                    } else { // handle settle order scenario: the tentative flag is updated from true to false
                        return orderService.settleQuote(order, new OrderServiceContext(order, new ApiContext()))
                    }
                } else { // order already settle
                    // determine the refund request
                    if (isARefund(oldOrder, order)) {
                        if (!AuthorizeContext.hasRights('refund')) {
                            throw AppCommonErrors.INSTANCE.forbidden().exception()
                        }

                        LOGGER.info('name=Refund_Or_Cancel_Non_Tentative_Offer')
                        oldOrder.orderItems = order.orderItems
                        return orderService.refundOrCancelOrder(oldOrder, new OrderServiceContext(order, new ApiContext())).then { Order refundedOrder ->
                            //todo
                            csrActionAudit('USD 10')
                            return Promise.pure(refundedOrder)
                        }
                    }
                    LOGGER.info('name=Update_Non_Tentative_Offer')
                    // update shipping address after settlement
                    if (allowModification(oldOrder, order)) {
                        oldOrder.shippingAddress = order.shippingAddress
                        oldOrder.shippingToPhone = order.shippingToPhone
                        oldOrder.shippingToName = order.shippingToName
                        return orderService.updateNonTentativeOrder(oldOrder,
                                new OrderServiceContext(order, new ApiContext()))
                    }
                    LOGGER.error('name=Update_Not_Allow')
                    throw AppErrors.INSTANCE.invalidSettledOrderUpdate().exception()
                }
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
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            Results<Order> results = new Results<>()
            if (!AuthorizeContext.hasRights('read')) {
                results.setItems([])
                return Promise.pure(results)
            }

            return orderService.getOrdersByUserId(userId.value, new OrderServiceContext(), orderQueryParam, pageParam).syncThen { List<Order> orders ->
                results.setItems(orders)
                return results
            }
        }
    }

    private void csrActionAudit(String info) {
        if (AuthorizeContext.hasScopes('csr')) {
            csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.RefundIssued, property: info)).get()
        }
    }
}
