/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.common.OrderStatusBuilder
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.ApiContext
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@TypeChecked
@Service('orderService')
class OrderServiceImpl implements OrderService {
    @Qualifier('orderFacadeContainer')
    @Autowired
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository
    @Autowired
    FlowSelector flowSelector
    @Autowired
    FlowExecutor flowExecutor
    @Qualifier('orderTransactionHelper')
    @Autowired
    TransactionHelper transactionHelper
    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl)


    void setFlowSelector(FlowSelector flowSelector) {
        this.flowSelector = flowSelector
    }

    @Override
    Promise<Order> settleQuote(Order order, ApiContext context) {
        LOGGER.info('name=Settle_Tentative_Order. userId: {}', order.user.value)
        order.tentative = false
        orderValidator.validateSettleOrderRequest(order)

        def orderServiceContext = initOrderServiceContext(order)
        Throwable error
        flowSelector.select(orderServiceContext, OrderServiceOperation.SETTLE_TENTATIVE).then { FlowType flowType ->
            // Prepare Flow Request
            Map<String, Object> requestScope = [:]
            def orderActionContext = new OrderActionContext()
            orderActionContext.orderServiceContext = orderServiceContext
            orderActionContext.trackingUuid = order.trackingUuid
            requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
            executeFlow(flowType, orderServiceContext, requestScope)
        }.syncRecover { Throwable throwable ->
            error = throwable
        }.syncThen {
            if (orderServiceContext.order.tentative) {
                LOGGER.info('name=Order_RollBack_To_Tentative, orderId={}', orderServiceContext.order.id)
                transactionHelper.executeInTransaction {
                    orderRepository.updateOrder(orderServiceContext.order, true)
                }
            }
            refreshOrderStatus(orderServiceContext.order)
            if (error != null) {
                throw error
            }
            return orderServiceContext.order
        }
    }

    @Override
    Promise<Order> updateTentativeOrder(Order order, ApiContext context) {
        LOGGER.info('name=Update_Tentative_Order. orderId: {}', order.id.value)

        setHonoredTime(order)
        def orderServiceContext = initOrderServiceContext(order)
        prepareOrder(order).then {
            flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE_TENTATIVE).then { FlowType flowType ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = order.trackingUuid
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                executeFlow(flowType, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.order
            }
        }
    }

    @Override
    Promise<Order> createQuote(Order order, ApiContext context) {
        LOGGER.info('name=Create_Tentative_Order. userId: {}', order.user.value)

        order.id = null
        setHonoredTime(order)

        def orderServiceContext = initOrderServiceContext(order)
        prepareOrder(order).then {
            flowSelector.select(orderServiceContext, OrderServiceOperation.CREATE_TENTATIVE).then { FlowType flowType ->
                // Prepare Flow Request
                assert (flowType != null)
                LOGGER.info('name=Create_Tentative_Order. flowType: {}', flowType)
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = order.trackingUuid
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                executeFlow(flowType, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.order
            }
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

    @Override
    Promise<Order> rateOrder(Order order, Boolean save) {
        return rateOrder(order).syncThen {
            if (save) {
                updateTentativeOrder(order, null)
            }
            return order
        }
    }

    @Override
    Promise<Order> rateOrder(Order order) {
        return facadeContainer.ratingFacade.rateOrder(order).syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_Rating_Error', throwable)
            // TODO parse the rating error
            throw AppErrors.INSTANCE.ratingConnectionError().exception()
        }.then { OrderRatingRequest ratingResult ->
            // todo handle rating violation
            if (ratingResult == null) {
                // TODO: log order charge action error?
                LOGGER.error('name=Rating_Result_Null')
                throw AppErrors.INSTANCE.ratingResultInvalid().exception()
            }
            CoreBuilder.fillRatingInfo(order, ratingResult)
            //  no need to log event for rating
            // call billing to calculate tax
            if (order.totalAmount == 0) {
                LOGGER.info('name=Skip_Calculate_Tax_Zero_Total_Amount')
                return Promise.pure(order)
            }
            if (CoreUtils.hasPhysicalOffer(order)) {
                // check whether the shipping method id and shipping address id are there
                if (order.shippingAddressId == null) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_shippingAddressId')
                        return Promise.pure(order)
                    }
                    LOGGER.error('name=Missing_shippingAddressId_To_Calculate_Tax')
                    throw AppErrors.INSTANCE.missingParameterField('shippingAddressId').exception()
                }
            } else {
                // check pi is there
                if (CollectionUtils.isEmpty(order.paymentInstruments)) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_PI')
                        return Promise.pure(order)
                    }
                    LOGGER.error('name=Missing_paymentInstruments_To_Calculate_Tax')
                    throw AppErrors.INSTANCE.missingParameterField('paymentInstruments').exception()
                }
            }
            return facadeContainer.billingFacade.quoteBalance(
                    CoreBuilder.buildBalance(order, BalanceType.DEBIT)).syncRecover {
                Throwable throwable ->
                    LOGGER.error('name=Fail_To_Calculate_Tax', throwable)
                    // TODO parse the tax error
                    throw AppErrors.INSTANCE.billingConnectionError().exception()
            }.then { Balance balance ->
                if (balance == null) {
                    // TODO: log order charge action error?
                    LOGGER.info('name=Fail_To_Calculate_Tax_Balance_Not_Found')
                    throw AppErrors.INSTANCE.balanceNotFound().exception()
                } else {
                    CoreBuilder.fillTaxInfo(order, balance)
                }
                return Promise.pure(order)
            }
        }
    }

    private Order completeOrder(Order order) {
        // order items
        order.orderItems = orderRepository.getOrderItems(order.id.value)
        if (order.orderItems == null) {
            throw AppErrors.INSTANCE.orderItemNotFound().exception()
        }
        // payment instrument
        order.setPaymentInstruments(orderRepository.getPaymentInstrumentIds(order.id.value))
        // discount
        order.setDiscounts(orderRepository.getDiscounts(order.id.value))
        refreshOrderStatus(order)
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

    @Override
    @Transactional
    Order getOrderByTrackingUuid(UUID trackingUuid) {
        if (trackingUuid == null) {
            return null
        }
        def order = orderRepository.getOrderByTrackingUuid(trackingUuid)
        if (order != null) {
            completeOrder(order)
        }
        return order
    }

    private void refreshOrderStatus(Order order) {
        transactionHelper.executeInTransaction {
            def status = OrderStatusBuilder.buildOrderStatus(order,
                    orderRepository.getOrderEvents(order.id.value))
            if (status != order.status) {
                order.status = status
                orderRepository.updateOrder(order, true)
            }
        }
    }

    private Promise<OrderServiceContext> executeFlow(
            FlowType flowType, OrderServiceContext context,
            Map<String, Object> requestScope) {
        def scope = requestScope
        if (requestScope == null) {
            scope = ActionUtils.initRequestScope(context)
        }
        scope.put(ActionUtils.REQUEST_FLOW_TYPE, (Object) flowType)
        return flowExecutor.start(flowType.name(), scope).syncRecover { Throwable throwable ->
            LOGGER.error('name=Flow_Execution_Failed. flowType: ' + flowType, throwable)
            if (throwable instanceof AppErrorException) {
                throw throwable
            } else {
                throw AppErrors.INSTANCE.unexpectedError().exception()
            }
        }.syncThen {
            return context
        }
    }

    private OrderServiceContext initOrderServiceContext(Order order) {
        OrderServiceContext context = new OrderServiceContext(order)
        return context
    }

    private void setHonoredTime(Order order) {
        def ht = new Date()
        LOGGER.info('name=Refresh_Order_Honored_Time. time: {}', ht)
        order.honoredTime = ht
        order.orderItems?.each { OrderItem oi ->
            oi.honoredTime = ht
        }
    }

    private Promise<Object> prepareOrder(Order order) {
        Promise.each(order.orderItems.iterator()) { OrderItem item -> // get item type from catalog
            facadeContainer.catalogFacade.getOffer(item.offer.value).syncThen { OrderOffer offer ->
                if (offer == null) {
                    throw AppErrors.INSTANCE.offerNotFound(item.offer.value?.toString()).exception()
                }
                item.type = CoreUtils.getOfferType(offer).name()
            }
        }.syncThen {
            return null
        }
    }
}