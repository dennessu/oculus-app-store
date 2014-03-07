/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
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
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        def orderServiceContext = initOrderServiceContext(order, null)
        flowSelector.select(orderServiceContext, OrderServiceOperation.CREATE).then { FlowType flowType ->
            executeFlow(flowType, orderServiceContext, null)
        }.syncThen {
            return [orderServiceContext.order]
        }
    }

    @Override
    @Transactional
    Promise<List<Order>> settleQuote(Order order, ApiContext context) {
        def orderServiceContext = initOrderServiceContext(order, null)
        flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE_TENTATIVE).then { FlowType flowType ->
            executeFlow(flowType, orderServiceContext, null)
        }.syncThen {
            return [orderServiceContext.order]
        }
    }

    @Override
    @Transactional
    Promise<Order> updateTentativeOrder(Order order, ApiContext context) {
        return null
    }

    @Override
    @Transactional
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
        def balanceRequest = CoreBuilder.buildBalance(initOrderServiceContext(order, null), BalanceType.DEBIT)
        billingFacade.quoteBalance(balanceRequest).syncThen { Balance balance ->
            order.isTaxInclusive = balance.taxIncluded
            order.totalTax = balance.taxAmount
            // TODO append item level tax
        }
        orders.add(order)

        return Promise.pure(orders)
    }

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(Long orderId) {
        def orderServiceContext = initOrderServiceContext(null, orderId)
        flowSelector.select(orderServiceContext, OrderServiceOperation.GET).syncThen { FlowType flowType ->

            executeFlow(flowType, orderServiceContext, null).syncThen { List<Order> orders ->
                if (CollectionUtils.isEmpty(orders)) {
                    throw AppErrors.INSTANCE.orderNotFound().exception()
                }
                def order = orderServiceContext.order
                // order items
                order.setOrderItems(orderRepository.getOrderItems(orderId))
                if (CollectionUtils.isEmpty(order.orderItems)) {
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
                order.setPaymentInstruments(orderRepository.getPaymentInstrumentIds(orderId))
                // discount
                order.setDiscounts(orderRepository.getDiscounts(orderId))
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

    private Promise<OrderServiceContext> executeFlow(FlowType flowType, OrderServiceContext context,
                                                     Map<String, Object> requestScope) {
        return flowExecutor.start(flowType.name(), ActionUtils.initRequestScope(context, requestScope)).syncThen {
            return context
        }
    }

    private OrderServiceContext initOrderServiceContext(Order order, Long orderId) {
        OrderServiceContext context = new OrderServiceContext()
        context.order = order
        context.orderId = orderId
        context.orderRepository = orderRepository
        context.billingFacade = billingFacade
        context.fulfillmentFacade = fulfillmentFacade
        context.identityFacade = identityFacade
        context.ratingFacade = ratingFacade
        return context
    }
}