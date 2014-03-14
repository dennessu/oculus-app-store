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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    Promise<List<Order>> createQuotes(Order order, ApiContext context) {

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
        def orderServiceContext = initOrderServiceContext(null)
        Map<String, Object> requestScope = ['GetOrderAction_OrderId':(Object)orderId]
        return flowSelector.select(orderServiceContext, OrderServiceOperation.GET).then { FlowType flowType ->
            executeFlow(flowType, orderServiceContext, requestScope).then { OrderServiceContext context ->
                if (context.order == null) {
                      throw AppErrors.INSTANCE.orderNotFound().exception()
                }
                // TODO need refactor the get logic
                // order items
                context.order.setOrderItems(orderRepository.getOrderItems(orderId))
                // rating info
                context.order.totalAmount = 0
                context.order.orderItems?.each { OrderItem orderItem ->
                    if (orderItem.totalAmount != null) {
                        context.order.totalAmount += orderItem.totalAmount
                    }
                }
                // payment instrument
                context.order.setPaymentInstruments(orderRepository.getPaymentInstrumentIds(orderId))
                // discount
                context.order.setDiscounts(orderRepository.getDiscounts(orderId))
                return Promise.pure(context.order)
            }
        }
    }

    @Override
    @Transactional
    Promise<Order> cancelOrder(Order request) {
        return null
    }

    @Override
    @Transactional
    Promise<Order> refundOrder(Order request) {
        return null
    }

    @Override
    @Transactional
    Promise<List<Order>> getOrders(Order request) {
        return null
    }

    @Override
    @Transactional
    Promise<OrderEvent> updateOrderBillingStatus(OrderEvent event) {
        return null
    }

    @Override
    @Transactional
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