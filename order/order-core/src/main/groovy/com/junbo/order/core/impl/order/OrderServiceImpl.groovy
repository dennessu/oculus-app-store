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
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
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
    OrderRepositoryFacade orderRepository
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

        // rate the order
        return getOrderByOrderId(order.getId().value, true).then { Order ratedOrder ->
            if (ratedOrder.status == OrderStatus.PRICE_RATING_CHANGED.name()) {
                throw AppErrors.INSTANCE.orderPriceChanged().exception()
            }
            def orderServiceContext = initOrderServiceContext(ratedOrder, context)
            Throwable error
            return flowSelector.select(orderServiceContext, OrderServiceOperation.SETTLE_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = UUID.randomUUID()
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                return executeFlow(flowName, orderServiceContext, requestScope)
            }.syncRecover { Throwable throwable ->
                error = throwable
            }.syncThen {
                def result = orderInternalService.refreshOrderStatus(orderServiceContext.order)
                if (error != null) {
                    throw error
                }
                return result
            }
        }
    }

    @Override
    Promise<Order> updateTentativeOrder(Order order, ApiContext context) {
        LOGGER.info('name=Update_Tentative_Order. orderId: {}', order.getId().value)

        setHonoredTime(order)
        def orderServiceContext = initOrderServiceContext(order, context)
        return prepareOrder(order).then {
            return flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = UUID.randomUUID()
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                return executeFlow(flowName, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.order
            }
        }
    }

    @Override
    Promise<Order> updateNonTentativeOrder(Order order, ApiContext context) {
        LOGGER.info('name=Update_Non_Tentative_Order. orderId: {}', order.getId().value)
        def orderServiceContext = initOrderServiceContext(order, context)
        return prepareOrder(order).then {
            return flowSelector.select(
                    orderServiceContext, OrderServiceOperation.UPDATE_NON_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = UUID.randomUUID()
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                return executeFlow(flowName, orderServiceContext, requestScope)
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

        def orderServiceContext = initOrderServiceContext(order, context)
        return prepareOrder(order).then {
            return flowSelector.select(orderServiceContext, OrderServiceOperation.CREATE_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                assert (flowName != null)
                LOGGER.info('name=Create_Tentative_Order. flowName: {}', flowName)
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = UUID.randomUUID()
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                return executeFlow(flowName, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.order
            }
        }
    }

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(Long orderId, Boolean doRate = true) {
        return orderInternalService.getOrderByOrderId(orderId).then { Order order ->
            if (order.tentative && CoreUtils.isRateExpired(order) && doRate) {
                // if the price rating result changes, return expired.
                Order oldRatingInfo = CoreUtils.copyOrderRating(order)
                order.honoredTime = new Date()
                return orderInternalService.rateOrder(order).then { Order o ->
                    if(!CoreUtils.compareOrderRating(oldRatingInfo, o)) {
                        o.status = OrderStatus.PRICE_RATING_CHANGED
                    }
                    return Promise.pure(o)
                }
            }
            return Promise.pure(order)
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
    @Transactional
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        return orderInternalService.getOrdersByUserId(userId, orderQueryParam, pageParam)
    }

    @Override
    @Transactional
    Promise<OrderEvent> updateOrderByOrderEvent(OrderEvent event) {

        switch (event.action) {
            case OrderActionType.CANCEL.name():
                LOGGER.info('name=Cancel_Order. orderId: {}, action:{}, status{}',
                        event.order.value, event.action, event.status)
                break
            case OrderActionType.CHARGE.name():
                LOGGER.info('name=Update_Charge_Status. orderId: {}, action:{}, status{}',
                        event.order.value, event.action, event.status)
                break
            case OrderActionType.FULFILL.name():
                LOGGER.info('name=Update_Fulfillment_Status. orderId: {}, action:{}, status{}',
                        event.order.value, event.action, event.status)
                break
            default:
                LOGGER.error('name=Event_Action_Not_Supported. orderId: {}, action:{}, status{}',
                        event.order.value, event.action, event.status)
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
        }

        LOGGER.info('name=Update_Order_By_Order_Event. orderId: {}, action:{}, status{}',
                event.order.value, event.action, event.status)

        return getOrderByOrderId(event.order.value).then { Order order ->
            def orderServiceContext = initOrderServiceContext(order, null)
            orderServiceContext.orderEvent = event
            return flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE).then { String flowName ->
                // Prepare Flow Request
                assert (flowName != null)
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = UUID.randomUUID()
                event.trackingUuid = orderActionContext.trackingUuid
                event.eventTrackingUuid = UUID.randomUUID()
                orderRepository.createOrderEvent(event)
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                return executeFlow(flowName, orderServiceContext, requestScope)
            }.syncThen {
                return orderServiceContext.orderEvent
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

    private OrderServiceContext initOrderServiceContext(Order order, ApiContext apiContext) {
        OrderServiceContext context = new OrderServiceContext(order)
        context.apiContext = apiContext
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
        return Promise.each(order.orderItems) { OrderItem item -> // get item type from catalog
            return facadeContainer.catalogFacade.getOfferRevision(item.offer.value).syncThen { OrderOfferRevision offer ->
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