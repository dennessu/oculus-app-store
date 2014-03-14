/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.ApiContext
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@Service('orderService')
class OrderServiceImpl implements OrderService {
    @Autowired
    PaymentFacade paymentFacade
    @Autowired
    BillingFacade billingFacade
    @Autowired
    IdentityFacade identityFacade
    @Autowired
    RatingFacade ratingFacade
    @Autowired
    FulfillmentFacade fulfillmentFacade
    @Autowired
    OrderRepository orderRepository
    @Autowired
    FlowSelector flowSelector
    @Autowired
    FlowExecutor flowExecutor

    void setFlowSelector(FlowSelector flowSelector) {
        this.flowSelector = flowSelector
    }

    @Override
    Promise<List<Order>> createOrders(Order order, ApiContext context) {
        // TODO: split orders
        // TODO: expand external resources
        // TODO: change this flow to 2 steps:
        //      1. createQuote
        //      2. settleQuote
        def orderServiceContext = initOrderServiceContext(order)
        flowSelector.select(
                new OrderServiceContext(order), OrderServiceOperation.CREATE).syncThen { FlowType flowType ->
            executeFlow(flowType, orderServiceContext, null)
        }.syncThen {
            return [orderServiceContext.order]
        }
    }

    @Override
    Promise<List<Order>> settleQuote(Order order, ApiContext context) {
        order.tentative = false
        def orderServiceContext = initOrderServiceContext(order)
        flowSelector.select(orderServiceContext, OrderServiceOperation.SETTLE_TENTATIVE).then { FlowType flowType ->
            executeFlow(flowType, orderServiceContext, null)
        }.syncThen {
            orderRepository.updateOrder(order, true)
            return [orderServiceContext.order]
        }
    }

    @Override
    Promise<Order> updateTentativeOrder(Order order, ApiContext context) {
        def orderServiceContext = initOrderServiceContext(order)

        flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE_TENTATIVE).then { FlowType flowType ->
            // Prepare Flow Request
            Map<String, Object> requestScope = [:]
            def orderActionContext = new OrderActionContext()
            orderActionContext.orderActionType = OrderActionType.RATE
            orderActionContext.orderServiceContext = orderServiceContext
            orderActionContext.trackingUuid = order.trackingUuid
            requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object)orderActionContext)
            executeFlow(flowType, orderServiceContext, requestScope)
        }.syncThen {
            return orderServiceContext.order
        }
    }

    @Override
    Promise<List<Order>> createQuotes(Order order, ApiContext context) {

        def ht = new Date()
        order.honoredTime = ht
        order.orderItems.each { OrderItem oi ->
            oi.honoredTime = ht
        }
        def orderServiceContext = initOrderServiceContext(order)

        flowSelector.select(orderServiceContext, OrderServiceOperation.CREATE_TENTATIVE).then { FlowType flowType ->
            // Prepare Flow Request
            Map<String, Object> requestScope = [:]
            def orderActionContext = new OrderActionContext()
            orderActionContext.orderActionType = OrderActionType.RATE
            orderActionContext.orderServiceContext = orderServiceContext
            orderActionContext.trackingUuid = order.trackingUuid
            requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object)orderActionContext)
            executeFlow(flowType, orderServiceContext, requestScope)
        }.syncThen {
            return [orderServiceContext.order]
        }
    }

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(Long orderId) {

        if (orderId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('orderId', 'orderId cannot be null').exception()
        }

        // get Order by id
        def order = orderRepository.getOrder(orderId)
        if (order == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        return Promise.pure(completeOrder(order))
    }

    private Order completeOrder(Order order) {
        // order items
        order.orderItems = orderRepository.getOrderItems(order.id.value)
        if (order.orderItems == null) {
            throw AppErrors.INSTANCE.orderItemNotFound().exception()
        }
        // rating info
        order.totalAmount = 0
        order.orderItems?.each { OrderItem orderItem ->
            if (orderItem.totalAmount != null) {
                order.totalAmount += orderItem.totalAmount
            }
        }
        // payment instrument
        order.setPaymentInstruments(orderRepository.getPaymentInstrumentIds(order.id.value))
        // discount
        order.setDiscounts(orderRepository.getDiscounts(order.id.value))
        return order
    }

    @Override
    Promise<Order> cancelOrder(Order request) {
        return null
    }

    @Override
    Promise<Order> refundOrder(Order request) {
        return null
    }

    @Override
    @Transactional
    Promise<List<Order>> getOrdersByUserId(Long userId) {

        if (userId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', 'userId cannot be null').exception()
        }

        // get Orders by userId
        def orders = orderRepository.getOrdersByUserId(userId)
        if (CollectionUtils.isEmpty(orders)) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        orders.each { Order order ->
            completeOrder(order)
        }
        return Promise.pure(orders)
    }

    @Override
    Promise<OrderEvent> updateOrderBillingStatus(OrderEvent event) {
        return null
    }

    @Override
    Promise<OrderEvent> updateOrderFulfillmentStatus(OrderEvent event) {
        return null
    }

    private Promise<OrderServiceContext> executeFlow(
            FlowType flowType, OrderServiceContext context,
            Map<String, Object> requestScope) {
        def scope = ActionUtils.initRequestScope(context, requestScope)
        scope.put(ActionUtils.REQUEST_FLOW_TYPE, (Object)flowType)
        return flowExecutor.start(flowType.name(), scope).syncThen {
            return context
        }
    }

    private OrderServiceContext initOrderServiceContext(Order order) {
        OrderServiceContext context = new OrderServiceContext(order)
        return context
    }
}