package com.junbo.order.rest.resource
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.spec.def.CsrLogActionType
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.model.CsrRefundInfo
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.auth.OrderAuthorizeCallbackFactory
import com.junbo.order.core.OrderService
import com.junbo.order.core.common.JsonMarshaller
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.order.OrderServiceContext
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
        orderValidator.notNull(order, 'order').notNull(order.user, 'user').notNull(order.country, 'country')
            .notNull(order.currency, 'currency')
        LOGGER.info('name=OrderResourceImpl.createOrder. user: {}', order.user.value)
        def callback = authorizeCallbackFactory.create(order)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }
            LOGGER.info('name=authorize_complete. user: {}', order.user.value)
            Boolean isTentative = order.tentative
            if (!isTentative) {
                // must be free to skip 2 step checkout
                return orderService.createFreeOrder(order,
                        new OrderServiceContext(order, new ApiContext())).then { Order o ->
                    LOGGER.info('name=OrderResourceImpl.createOrder_Free_Complete. order: {}', o.getId().value)
                    return Promise.pure(o)
                }
            } else {
                return orderService.createQuote(order,
                        new OrderServiceContext(order, new ApiContext())).then { Order ratedOrder ->
                    LOGGER.info('name=OrderResourceImpl.createOrder_Complete. order: {}', ratedOrder.getId().value)
                    return Promise.pure(ratedOrder)
                }
            }
        }
    }

    @Override
    Promise<Order> updateOrderByOrderId(OrderId orderId, Order order) {
        orderValidator.notNull(order, 'order').notNull(order.user, 'user').notNull(orderId, 'orderId')
                .notNull(order.country, 'country').notNull(order.currency, 'currency')
        LOGGER.info('name=OrderResourceImpl.updateOrderByOrderId. orderId: {}', orderId.value)
        def callback = authorizeCallbackFactory.create(order)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }
            LOGGER.info('name=authorize_complete. user: {}', order.user.value)
            order.id = orderId
            return orderService.getOrderByOrderId(orderId.value, false, new OrderServiceContext(), false).then { Order oldOrder ->
                // handle the update request per scenario
                if (oldOrder.tentative) { // order not settled
                    if (order.tentative) {
                        // rate and update the tentative order
                        LOGGER.info('name=updateTentativeOrder, orderId: {}', orderId.value)
                        return orderService.updateTentativeOrder(order,
                                new OrderServiceContext(order, new ApiContext())).syncThen { Order result ->
                            return result
                        }
                    } else { // handle settle order scenario: the tentative flag is updated from true to false
                        if (!AuthorizeContext.hasRights('settle.quote')) {
                            throw AppCommonErrors.INSTANCE.forbidden().exception()
                        }
                        LOGGER.info('name=settleQuote, orderId: {}', orderId.value)
                        return orderService.settleQuote(order, new OrderServiceContext(order, new ApiContext()))
                    }
                } else { // order already settle
                    // determine the refund request
                    oldOrder.note = order.note
                    if (isARefund(oldOrder, order)) {
                        LOGGER.info('name=refund, orderId: {}', orderId.value)
                        if (!AuthorizeContext.hasRights('refund')) {
                            throw AppCommonErrors.INSTANCE.forbidden().exception()
                        }
                        LOGGER.info('name=authorize_refund_complete. user: {}', order.user.value)
                        oldOrder.orderItems = order.orderItems
                        oldOrder.totalTax = order.totalTax
                        return orderService.refundOrCancelOrder(oldOrder, new OrderServiceContext(oldOrder, new ApiContext())).then { Order refundedOrder ->
                            csrActionAudit(refundedOrder)
                            return Promise.pure(refundedOrder)
                        }
                    }
                    LOGGER.info('name=Update_Non_Tentative_Order')
                    // update shipping address after settlement
                    if (allowModification(oldOrder, order)) {
                        oldOrder.shippingAddress = order.shippingAddress
                        oldOrder.shippingToPhone = order.shippingToPhone
                        oldOrder.shippingToName = order.shippingToName
                        return orderService.updateNonTentativeOrder(oldOrder,
                                new OrderServiceContext(order, new ApiContext()))
                    }
                    LOGGER.error('name=Update_Not_Allowed')
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
            LOGGER.info('name=Order_Full_Refund, id:{}', olderOrder.getId().value)
            return true
        }

        if(olderOrder.orderItems.size() > newOrder.orderItems.size()) {
            LOGGER.info('name=Order_Partial_Refund, id:{}', olderOrder.getId().value)
            return true
        }
        if(olderOrder.totalTax > 0 && newOrder.totalTax == 0) {
            LOGGER.info('name=Order_Tax_Refund, id:{}', olderOrder.getId().value)
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
                LOGGER.info('name=Order_Partial_Refund_Quantity, id:{}', olderOrder.getId().value)
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

    private void csrActionAudit(Order order) {
        if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
            String info = ''
            if (order.billingHistories != null && order.billingHistories.last() != null) {
                BillingHistory billingHistory = order.billingHistories.last()
                CsrRefundInfo csrRefundInfo = new CsrRefundInfo()
                csrRefundInfo.currency = order.currency.value
                csrRefundInfo.success = billingHistory.success
                csrRefundInfo.amount = billingHistory.totalAmount
                info = JsonMarshaller.marshall(csrRefundInfo)
            }

            csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Refund', action: CsrLogActionType.RefundIssued, property: info)).get()
        }
    }
}
