/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.common.*
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

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
    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService
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
        flowSelector.select(orderServiceContext, OrderServiceOperation.SETTLE_TENTATIVE).then { String flowName ->
            // Prepare Flow Request
            Map<String, Object> requestScope = [:]
            def orderActionContext = new OrderActionContext()
            orderActionContext.orderServiceContext = orderServiceContext
            orderActionContext.trackingUuid = order.trackingUuid
            requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
            executeFlow(flowName, orderServiceContext, requestScope)
        }.syncRecover { Throwable throwable ->
            error = throwable
        }.syncThen {
            orderInternalService.refreshOrderStatus(orderServiceContext.order)
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
            flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = order.trackingUuid
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                executeFlow(flowName, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.order
            }
        }
    }

    @Override
    Promise<Order> updateNonTentativeOrder(Order order, ApiContext context) {
        LOGGER.info('name=Update_Non_Tentative_Order. orderId: {}', order.id.value)
        def orderServiceContext = initOrderServiceContext(order)
        prepareOrder(order).then {
            flowSelector.select(
                    orderServiceContext, OrderServiceOperation.UPDATE_NON_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = order.trackingUuid
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                executeFlow(flowName, orderServiceContext, requestScope)
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
            flowSelector.select(orderServiceContext, OrderServiceOperation.CREATE_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                assert (flowName != null)
                LOGGER.info('name=Create_Tentative_Order. flowName: {}', flowName)
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = order.trackingUuid
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                executeFlow(flowName, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.order
            }
        }
    }

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(Long orderId) {
        return orderInternalService.getOrderByOrderId(orderId)
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
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        return orderInternalService.getOrdersByUserId(userId, orderQueryParam, pageParam)
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
    Order getOrderByTrackingUuid(UUID trackingUuid, Long userId) {
        return orderInternalService.getOrderByTrackingUuid(trackingUuid, userId)
    }

    @Override
    Promise<Order> completeChargeOrder(Long orderId, ApiContext context) {
        LOGGER.info('name=Complete_Charge_Order. orderId: {}', orderId)
        return getOrderByOrderId(orderId).then { Order order ->
            def orderServiceContext = initOrderServiceContext(order)
            flowSelector.select(orderServiceContext, OrderServiceOperation.COMPLETE_CHARGE).then { String flowName ->
                // Prepare Flow Request
                assert (flowName != null)
                LOGGER.info('name=Complete_Charge_Order. flowName: {}', flowName)
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = order.trackingUuid
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                executeFlow(flowName, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.order
            }
        }
    }

    private Promise<OrderServiceContext> executeFlow(
            String flowName, OrderServiceContext context,
            Map<String, Object> requestScope) {
        def scope = requestScope
        if (requestScope == null) {
            scope = ActionUtils.initRequestScope(context)
        }
        scope.put(ActionUtils.REQUEST_FLOW_NAME, (Object) flowName)
        return flowExecutor.start(flowName, scope).syncRecover { Throwable throwable ->
            LOGGER.error('name=Flow_Execution_Failed. flowName: ' + flowName, throwable)
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