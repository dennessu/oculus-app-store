/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.catalog.spec.model.offer.OfferRevision
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
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.order.spec.model.enums.OrderStatus
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
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl)


    void setFlowSelector(FlowSelector flowSelector) {
        this.flowSelector = flowSelector
    }

    @Override
    Promise<Order> settleQuote(Order order, OrderServiceContext orderServiceContext) {
        // order is the request

        LOGGER.info('name=Settle_Tentative_Order. userId: {}', order.user.value)
        orderValidator.validateSettleOrderRequest(order)

        // rate the order
        // get the existing order with new rating
        return getOrderByOrderId(order.getId().value, true, orderServiceContext).then { Order ratedOrder ->
            orderServiceContext.order = ratedOrder
            if (ratedOrder.status == OrderStatus.PRICE_RATING_CHANGED.name()) {
                throw AppErrors.INSTANCE.orderPriceChanged().exception()
            }

            // TODO: compare the reqeust and the order persisted
            orderValidator.validateSettleOrderRequest(ratedOrder)
            ratedOrder.payments[0].successRedirectUrl = order.payments[0].successRedirectUrl
            ratedOrder.payments[0].cancelRedirectUrl = order.payments[0].cancelRedirectUrl
            ratedOrder.purchaseTime = ratedOrder.honoredTime

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
            }.then {
                def result = orderInternalService.refreshOrderStatus(orderServiceContext.order,
                        !orderServiceContext.isAsyncCharge) // In asyncCharge case, update status via order event
                if (error != null) {
                    throw error
                }
                return getOrderByOrderId(result.getId().value, false, orderServiceContext)
            }
        }
    }

    @Override
    Promise<Order> updateTentativeOrder(Order order, OrderServiceContext orderServiceContext) {
        LOGGER.info('name=Update_Tentative_Order. orderId: {}', order.getId().value)

        setHonoredTime(order)
        return prepareOrder(order, orderServiceContext).then {
            return flowSelector.select(orderServiceContext, OrderServiceOperation.UPDATE_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = UUID.randomUUID()
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                return executeFlow(flowName, orderServiceContext, requestScope)
            }.then {
                return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext)
            }
        }
    }

    @Override
    Promise<Order> updateNonTentativeOrder(Order order, OrderServiceContext orderServiceContext) {
        LOGGER.info('name=Update_Non_Tentative_Order. orderId: {}', order.getId().value)
        return prepareOrder(order , orderServiceContext).then {
            return flowSelector.select(
                    orderServiceContext, OrderServiceOperation.UPDATE_NON_TENTATIVE).then { String flowName ->
                // Prepare Flow Request
                Map<String, Object> requestScope = [:]
                def orderActionContext = new OrderActionContext()
                orderActionContext.orderServiceContext = orderServiceContext
                orderActionContext.trackingUuid = UUID.randomUUID()
                requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                return executeFlow(flowName, orderServiceContext, requestScope)
            }.then {
                return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext)
            }
        }
    }

    @Override
    Promise<Order> createQuote(Order order, OrderServiceContext orderServiceContext) {
        LOGGER.info('name=Create_Tentative_Order. userId: {}', order.user.value)

        order.id = null
        setHonoredTime(order)

        return prepareOrder(order, orderServiceContext).then {
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
            }.then {
                return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext)
            }
        }
    }

    @Override
    Promise<Order> getOrderByOrderId(Long orderId, Boolean doRate = true, OrderServiceContext context) {
        return orderInternalService.getOrderByOrderId(orderId, context).then { Order order ->
            if (doRate) {
                return refreshTentativeOrderPrice(order)
            }
            return Promise.pure(order)
        }
    }

    @Override
    Promise<Order> cancelOrder(Order request, OrderServiceContext orderServiceContext) {
        return null
    }

    @Override
    Promise<Order> refundOrder(Order request, OrderServiceContext orderServiceContext) {
        // order is the request
        LOGGER.info('name=Refund_Order. orderId: {}', request.getId().value)
        orderValidator.validateRefundOrderRequest(request)

        return flowSelector.select(
                orderServiceContext, OrderServiceOperation.REFUND).then { String flowName ->
            // Prepare Flow Request
            Map<String, Object> requestScope = [:]
            def orderActionContext = new OrderActionContext()
            orderActionContext.orderServiceContext = orderServiceContext
            orderActionContext.trackingUuid = UUID.randomUUID()
            requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
            return executeFlow(flowName, orderServiceContext, requestScope)
        }.then {
            return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext)
        }
    }

    @Override
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderServiceContext orderServiceContext, OrderQueryParam orderQueryParam, PageParam pageParam) {
        return orderInternalService.getOrdersByUserId(userId, orderServiceContext, orderQueryParam, pageParam).then { List<Order> orders ->
            List<Order> ods = []
            return Promise.each(orders) { Order order ->
                if (order.tentative) {
                    return refreshTentativeOrderPrice(order).then { Order o ->
                        ods << o
                        return Promise.pure(o)
                    }
                } else {
                    ods << order
                    return Promise.pure(order)
                }
            }.then {
                return Promise.pure(ods)
            }
        }
    }

    @Override
    @Transactional
    Promise<OrderEvent> updateOrderByOrderEvent(OrderEvent event, OrderServiceContext orderServiceContext) {

        switch (event.action) {
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

        return getOrderByOrderId(event.order.value, orderServiceContext).then { Order order ->
            orderServiceContext.order = order
            orderServiceContext.orderEvent = event
            if (!CoreUtils.isPendingOnEvent(order, event)) {
                throw AppErrors.INSTANCE.orderEvenStatusNotMatch().exception()
            }
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
            }.then {
                orderInternalService.refreshOrderStatus(orderServiceContext.order, true)
                return Promise.pure(orderServiceContext.orderEvent)
            }
        }
    }

    private Promise<Order> refreshTentativeOrderPrice(Order order) {
        if (order.tentative && CoreUtils.isRateExpired(order)) {
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
            } else if (throwable instanceof AssertionError ){
                throw AppErrors.INSTANCE.unexpectedError('Unexpected assertion failure').exception()
            } else {
                throw AppErrors.INSTANCE.unexpectedError(throwable.message).exception()
            }
        }.syncThen {
            return context
        }
    }

    private void setHonoredTime(Order order) {
        def ht = new Date()
        LOGGER.info('name=Refresh_Order_Honored_Time. time: {}', ht)
        order.honoredTime = ht
        order.orderItems?.each { OrderItem oi ->
            oi.honoredTime = ht
        }
    }

    private Promise<Object> prepareOrder(Order order, OrderServiceContext context) {
        return Promise.each(order.orderItems) { OrderItem item -> // get item type from catalog
            return orderServiceContextBuilder.getOffer(item.offer, context).syncThen {
                OrderOfferRevision offer ->
                if (offer == null) {
                    throw AppErrors.INSTANCE.offerNotFound(item.offer.value?.toString()).exception()
                }
                item.type = CoreUtils.getOfferType(offer).name()
                item.isPreorder = CoreUtils.isPreorder(offer, order.country.value)
                updateOfferInfo(order, item, offer.catalogOfferRevision)
            }
        }.syncThen {
            return null
        }
    }

    private void updateOfferInfo(Order order, OrderItem item, OfferRevision offer) {
        String locale = order.locale.value?.replace('-', '_')
        item.offerOrganization = offer.ownerId?.value
        // add fallback logic here
        if (offer.locales == null) {
            order.paymentDescription = null
            item.offerName = null
            item.offerDescription = null
            return
        }
        String description = offer.locales[locale] != null ?
                offer.locales[locale].shortDescription : offer.locales['DEFAULT']?.shortDescription
        String name = offer.locales[locale] != null ?
                offer.locales[locale].shortDescription : offer.locales['DEFAULT']?.name
        item.offerName = name
        item.offerDescription = description
        if (order.paymentDescription == null || order.paymentDescription == '') {
            order.paymentDescription = description
        }
        else if (description != null) {
            order.paymentDescription += ' & ' + description
        }
    }
}