/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
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
        def serviceContext = new OrderServiceContext(order)
        flowSelector.select(
                new OrderServiceContext(order), OrderServiceOperation.CREATE).syncThen { FlowType flowType ->
            executeFlow(flowType, serviceContext, null)
        }
    }

    @Override
    Promise<List<Order>> settleQuote(Order order, ApiContext context) {
        def serviceContext = new OrderServiceContext(order)
        flowSelector.select(serviceContext, OrderServiceOperation.SETTLE_TENTATIVE).syncThen { FlowType flowType ->
            executeFlow(flowType, serviceContext, null)
        }
    }

    @Override
    Promise<Order> updateTentativeOrder(Order order, ApiContext context) {
        return null
    }

    @Override
    Promise<List<Order>> createQuotes(Order order, ApiContext context) {

        List<Order> orders = []
        // Only tentative order is accepted
        if (!order.tentative) {
            throw AppErrors.INSTANCE.fieldInvalid('tentative', 'tentative should be true').exception()
        }

        // Call rating to get the price and discount
        ratingFacade.rateOrder(
            order).syncThen { OrderRatingRequest request ->
                CoreBuilder.fillRatingInfo(order, request)
                // TODO append returned promotions to order
        }

        // Create order event for this operation
        OrderEvent orderEvent = new OrderEvent()
        orderEvent.status = EventStatus.COMPLETED.toString()
        orderEvent.action = OrderActionType.RATE.toString()

        // Persist order
        orderRepository.createOrder(order, orderEvent)

        // Calculate Tax
        def balanceRequest = CoreBuilder.buildBalance(new OrderServiceContext(order), BalanceType.DEBIT)
        billingFacade.quoteBalance(balanceRequest).syncThen { Balance balance ->
            order.isTaxInclusive = balance.taxIncluded
            order.totalTax = balance.taxAmount
            // TODO append item level tax
        }
        orders.add(order)

        return Promise.pure(orders)
    }

    @Override
    Promise<Order> getOrderByOrderId(Long orderId) {
        def order = new Order()
        order.id = new OrderId(orderId)
        def orderServiceContext = new OrderServiceContext(order)
        Map<String, Object> requestScope = ['GetOrderAction_OrderId':(Object)orderId]
        flowSelector.select(orderServiceContext, OrderServiceOperation.GET).syncThen { FlowType flowType ->
            executeFlow(flowType, orderServiceContext, requestScope).syncThen { List<Order> orders ->
                if (CollectionUtils.isEmpty(orders)) {
                    return null
                }
                // TODO need refactor the get logic
                // order items
                order.setOrderItems(orderRepository.getOrderItems(orderId))
                // rating info
                order.totalAmount = 0
                order.orderItems?.each { OrderItem orderItem ->
                    if (orderItem.totalAmount != null) {
                        order.totalAmount += orderItem.totalAmount
                    }
                }
                // payment instrument
                order.setPaymentInstruments(orderRepository.getPaymentInstrumentIds(orderId))
                // discount
                order.setDiscounts(orderRepository.getDiscounts(orderId))
                return order
            }
        }
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
    Promise<List<Order>> getOrders(Order request) {
        return null
    }

    @Override
    Promise<OrderEvent> updateOrderBillingStatus(OrderEvent event) {
        return null
    }

    @Override
    Promise<OrderEvent> updateOrderFulfillmentStatus(OrderEvent event) {
        return null
    }

    private Promise<OrderServiceContext> executeFlow(FlowType flowType, OrderServiceContext context,
                                                     Map<String, Object> requestScope) {
        return flowExecutor.start(flowType.name(), ActionUtils.initRequestScope(context, requestScope)).syncThen {
            ActionContext actionContext
            return ActionUtils.getOrderActionContext(actionContext).orderServiceContext
        }
    }
}