/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.common.id.PaymentInstrumentId
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
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
        // TODO: expand external resources
        expandOrder(order).syncThen { OrderServiceContext orderServiceContext ->
            return executeFlow(flowSelector.select(orderServiceContext, OrderServiceOperation.CREATE),
                    orderServiceContext, null)
        }
    }

    @Override
    Promise<List<Order>> settleQuote(Order order, ApiContext context) {
        expandOrder(order).syncThen { OrderServiceContext orderServiceContext ->
            return executeFlow(flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE_TENTATIVE),
                    orderServiceContext, null)
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

        // Expand the passed in ids
        expandTentativeOrder(order).syncThen { OrderServiceContext orderServiceContext ->
            // Call rating to get the price and discount
            ratingFacade.rateOrder(
                order, orderServiceContext?.shippingAddress?.country).syncThen { OrderRatingRequest request ->
                    CoreBuilder.fillRatingInfo(order, request)
                    // TODO append returned promotions to order
            }

            // Create order event for this operation
            OrderEvent orderEvent = new OrderEvent()
            orderEvent.status = EventStatus.COMPLETED.toString()
            orderEvent.action = OrderAction.RATE.toString()

            // Persist order
            orderRepository.createOrder(order, orderEvent)

            // Calculate Tax
            def balanceRequest = CoreBuilder.buildBalance(orderServiceContext, BalanceType.DEBIT)
            billingFacade.quoteBalance(balanceRequest).syncThen { Balance balance ->
                order.isTaxInclusive = balance.taxIncluded
                order.totalTax = balance.taxAmount
                // TODO append item level tax
            }
            orders.add(order)
        }
        return Promise.pure(orders)
    }

    @Override
    Promise<Order> getOrderByOrderId(Long orderId) {
        expandOrder(null).syncThen { OrderServiceContext context ->
            context.setOrderId(orderId)
            executeFlow(flowSelector.select(context, OrderServiceOperation.GET), context, null).syncThen {
                if (context.order == null) {
                    return Promise.pure(null)
                }
                def order = context.order
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

    @Override
    Promise<OrderServiceContext> expandOrder(Order order) {
        def context = new OrderServiceContext()
        context.setOrder(order)
        context.paymentInstruments = []
        Promise.each(order.paymentInstruments?.iterator()) { PaymentInstrumentId pmId ->
            paymentFacade.getPaymentInstrument(pmId.value).syncThen { PaymentInstrument pi ->
                context.paymentInstruments << pi
            }
        }.then {
            context.orderRepository = orderRepository
            context.paymentFacade = paymentFacade
            context.billingFacade = billingFacade
            context.identityFacade = identityFacade
            context.ratingFacade = ratingFacade
            Promise.pure(context)
        }
    }

    @Override
    Promise<OrderServiceContext> expandTentativeOrder(Order order) {

        OrderServiceContext context = new OrderServiceContext()
        context.setOrder(order)
        context.setBillingFacade(billingFacade)
        // TODO support item level shipping address
        Promise shippingAddressPromise = billingFacade.getShippingAddress(order?.shippingAddressId)
        shippingAddressPromise.syncThen(new Promise.Func<ShippingAddress, Promise>() {
            @Override
            Promise apply(ShippingAddress shippingAddress) {
                context.shippingAddress = shippingAddress
                return Promise.pure(shippingAddress)
            }
        } )
        return Promise.pure(context)
    }

    private Promise<OrderServiceContext> executeFlow(FlowType flowType, OrderServiceContext context,
                                                     Map<String, Object> requestScope) {
        return flowExecutor.start(flowType.name(), ActionUtils.initRequestScope(context, requestScope)).syncThen {
            ActionContext actionContext
            return ActionUtils.getOrderActionContext(actionContext).orderServiceContext
        }
    }

}