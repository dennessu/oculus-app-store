/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.order.spec.model.enums.OrderStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@TypeChecked
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

    Integer offerCountLimitation
    Integer itemCountLimitation

    void setFlowSelector(FlowSelector flowSelector) {
        this.flowSelector = flowSelector
    }

    @Override
    Promise<Order> settleQuote(Order order, OrderServiceContext orderServiceContext) {
        // order is the request

        LOGGER.info('name=Settle_Tentative_Order. userId: {}', order.user.value)
        // rate the order
        // get the existing order with new rating
        return getOrderByOrderId(order.getId().value, true, orderServiceContext, false).then { Order ratedOrder ->
            return validateDuplicatePurchase(ratedOrder, orderServiceContext).then {
                orderServiceContext.order = ratedOrder
                if (ratedOrder.status == OrderStatus.PRICE_RATING_CHANGED.name()) {
                    throw AppErrors.INSTANCE.orderPriceChanged().exception()
                }
                // TODO: compare the reqeust and the order persisted
                orderValidator.validateSettleOrderRequest(ratedOrder)

                if (order.payments.size() > 0 && ratedOrder.payments.size() > 0) {
                    ratedOrder.payments[0].successRedirectUrl = order.payments[0].successRedirectUrl
                    ratedOrder.payments[0].cancelRedirectUrl = order.payments[0].cancelRedirectUrl
                }

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
                    if (error != null) {
                        throw error
                    }
                    return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext, true)
                }
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
                LOGGER.info('name=updateTentativeOrder_Completed')
                return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext, true)
            }
        }
    }

    @Override
    Promise<Order> updateNonTentativeOrder(Order order, OrderServiceContext orderServiceContext) {
        LOGGER.info('name=Update_Non_Tentative_Order. orderId: {}', order.getId().value)
        return prepareOrder(order, orderServiceContext).then {
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
                LOGGER.info('name=updateNonTentativeOrder_Completed')
                return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext, true)
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
                LOGGER.info('name=createQuote_Completed')
                return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext, true)
            }
        }
    }

    @Override
    Promise<Order> createFreeOrder(Order order, OrderServiceContext orderServiceContext) {
        LOGGER.info('name=Create_Free_Order. userId: {}', order.user.value)

        order.id = null
        setHonoredTime(order)
        return prepareOrder(order, orderServiceContext).then {
            return orderInternalService.rateOrder(order, orderServiceContext).then { Order ratedOrder ->
                if (!CoreUtils.isFreeOrder(ratedOrder)) {
                    throw AppErrors.INSTANCE.notFreeOrder().exception()
                }
                LOGGER.info('name=Order_is_free')
                //return validateDuplicatePurchase(ratedOrder, orderServiceContext).then {
                orderServiceContext.order = ratedOrder

                // TODO: compare the request and the order persisted
                orderValidator.validateSettleOrderRequest(ratedOrder)

                ratedOrder.purchaseTime = ratedOrder.honoredTime
                return flowSelector.select(orderServiceContext, OrderServiceOperation.SETTLE_FREE).then { String flowName ->
                    // Prepare Flow Request
                    Map<String, Object> requestScope = [:]
                    def orderActionContext = new OrderActionContext()
                    orderActionContext.orderServiceContext = orderServiceContext
                    orderActionContext.trackingUuid = UUID.randomUUID()
                    requestScope.put(ActionUtils.SCOPE_ORDER_ACTION_CONTEXT, (Object) orderActionContext)
                    return executeFlow(flowName, orderServiceContext, requestScope)
                }.then {
                    return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext, true).then { Order o ->
                        LOGGER.info('name=Create_Free_Order_completed. orderId: {}', o.getId().value)
                        return Promise.pure(o)
                    }
                }
                //}
            }
        }
    }

    @Override
    Promise<Order> getOrderByOrderId(Long orderId, Boolean doRate = true, OrderServiceContext context, Boolean updateOrderStatus) {
        LOGGER.info('name=getOrderByOrderId')
        return orderInternalService.getOrderByOrderId(orderId, context, updateOrderStatus).then { Order order ->
            if (doRate) {
                return refreshTentativeOrderPrice(order, context)
            }
            return Promise.pure(order)
        }.then { Order o ->
            LOGGER.info('name=getOrderByOrderId_Completed')
            return Promise.pure(o)
        }
    }

    @Override
    Promise<Order> refundOrCancelOrder(Order request, OrderServiceContext orderServiceContext) {
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
            LOGGER.info('name=refundOrCancelOrder_Completed')
            return getOrderByOrderId(orderServiceContext.order.getId().value, false, orderServiceContext, true)
        }
    }

    @Override
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderServiceContext orderServiceContext, OrderQueryParam orderQueryParam, PageParam pageParam) {
        LOGGER.info('name=getOrdersByUserId. userId: {}', userId)
        return orderInternalService.getOrdersByUserId(userId, orderServiceContext, orderQueryParam, pageParam).then { List<Order> orders ->
            List<Order> ods = []
            return Promise.each(orders) { Order order ->
                if (order.tentative) {
                    return refreshTentativeOrderPrice(order, orderServiceContext).then { Order o ->
                        ods << o
                        return Promise.pure(o)
                    }
                } else {
                    ods << order
                    return Promise.pure(order)
                }
            }.then {
                LOGGER.info('name=getOrdersByUserId_completed. userId: {}', userId)
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
                if (event.status != EventStatus.COMPLETED.name())
                {
                    LOGGER.error('name=Event_Action_Not_Supported. orderId: {}, action:{}, status{}',
                            event.order.value, event.action, event.status)
                    throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
                }
                break
            default:
                LOGGER.error('name=Event_Action_Not_Supported. orderId: {}, action:{}, status{}',
                        event.order.value, event.action, event.status)
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
        }

        LOGGER.info('name=Update_Order_By_Order_Event. orderId: {}, action:{}, status{}',
                event.order.value, event.action, event.status)

        return getOrderByOrderId(event.order.value, orderServiceContext, false).then { Order order ->
            orderServiceContext.order = order
            orderServiceContext.orderEvent = event
            if (!CoreUtils.isPendingOnEvent(order, event)) {
                throw AppErrors.INSTANCE.orderEvenStatusNotMatch().exception()
            }
            if (CoreUtils.bypassEvent(order, event)) {
                return Promise.pure(event)
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
                LOGGER.info('name=updateOrderByOrderEvent_Completed')
                orderInternalService.refreshOrderStatus(order, true)
                return Promise.pure(orderServiceContext.orderEvent)
            }
        }
    }

    private Promise<Order> refreshTentativeOrderPrice(Order order, OrderServiceContext context) {
        LOGGER.info('name=refreshTentativeOrderPrice')
        if (order.tentative && CoreUtils.isRateExpired(order)) {
            // if the price rating result changes, return expired.
            Order oldRatingInfo = CoreUtils.copyOrderRating(order)
            order.honoredTime = new Date()
            return orderInternalService.rateOrder(order, context).then { Order o ->
                if (!CoreUtils.compareOrderRating(oldRatingInfo, o)) {
                    o.status = OrderStatus.PRICE_RATING_CHANGED
                }
                LOGGER.info('name=refreshTentativeOrderPrice_Completed')
                return Promise.pure(o)
            }
        }
        LOGGER.info('name=skip_refreshTentativeOrderPrice')
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
        LOGGER.info('name=start_flow. flowName: {}', flowName)
        return flowExecutor.start(flowName, scope).syncRecover { Throwable throwable ->
            LOGGER.error('name=Flow_Execution_Failed. flowName: ' + flowName, throwable)
            if (throwable instanceof AppErrorException) {
                throw throwable
            } else {
                throw AppCommonErrors.INSTANCE.internalServerError(new Exception(throwable)).exception()
            }
        }.syncThen {
            LOGGER.info('name=flow_completed. flowName: {}', flowName)
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

    private Promise<Void> prepareOrder(Order order, OrderServiceContext context) {
        LOGGER.info('name=PrepareOrder')
        mergeSameOrderItems(order)
        checkItemCount(order)
        return Promise.each(order.orderItems) { OrderItem item -> // get item type from catalog
            LOGGER.info('name=get_offers')
            return orderServiceContextBuilder.getOffer(item.offer, context).then { Offer offer ->
                if (offer == null) {
                    throw AppErrors.INSTANCE.offerNotFound(item.offer.value?.toString()).exception()
                }
                return orderInternalService.validateDuplicatePurchase(order, offer, item.quantity).syncThen {
                    item.type = offer.type.name()
                    item.isPreorder = CoreUtils.isPreorder(offer, order.country.value)
                    updateOfferInfo(order, item, offer)
                    item.offerOrganizationName = offer.owner?.name
                }
            }
        }.syncThen {
            LOGGER.info('name=PrepareOrder_Complete')
            return null
        }
    }

    private void mergeSameOrderItems (Order order) {
        def temp = new HashMap<OrderItem, Integer>()
        order.orderItems?.each { OrderItem oi ->
            temp[oi] = temp.get(oi, 0) + oi.quantity
        }
        order.orderItems = []
        temp.each { OrderItem k, Integer v ->
            k.quantity = v
            order.orderItems << k
            LOGGER.info('name=Print_Order_Item. offerId: {}, quantity: {}', k.offer.toString(), k.quantity)
        }
    }

    private void checkItemCount (Order order) {
        assert (order != null && order.orderItems != null)
        if (order.orderItems.size() > itemCountLimitation) {
            LOGGER.error('name=tooManyItems:' + order.orderItems.size() + ' should not exceed: ' + itemCountLimitation)
            throw AppErrors.INSTANCE.tooManyItems(itemCountLimitation).exception()
        }
        LOGGER.info('name=total_order_items: {}. limits: {}', order.orderItems.size(), itemCountLimitation)
        def offerCount = 0L
        order.orderItems.each { OrderItem oi ->
            offerCount += oi.quantity
        }
        if (offerCount > offerCountLimitation) {
            LOGGER.error('name=tooManyOffers:' + offerCount + ' should not exceed: ' + offerCountLimitation)
            throw AppErrors.INSTANCE.tooManyOffers(offerCountLimitation).exception()
        }
        LOGGER.info('name=total_offers: {}. limits: {}', offerCount, offerCountLimitation)
    }

    private Promise<Void> validateDuplicatePurchase(Order order, OrderServiceContext context) {
        def orderSnapshot = []
        return Promise.each(order.orderItems) { OrderItem item -> // get item type from catalog
            return orderServiceContextBuilder.getOffer(item.offer, context).then { Offer offer ->
                if (offer == null) {
                    throw AppErrors.INSTANCE.offerNotFound(item.offer.value?.toString()).exception()
                }
                orderSnapshot << CoreBuilder.buildOfferSnapshot(offer)
                return orderInternalService.validateDuplicatePurchase(order, offer, item.quantity)
            }
        }.syncThen {
            order.orderSnapshot = orderSnapshot
            orderInternalService.persistOrderSnapshot(order)
            return null
        }
    }

    private void updateOfferInfo(Order order, OrderItem item, Offer offer) {
        String locale = order.locale.value?.replace('-', '_')
        item.offerOrganization = offer.owner?.getId()?.value

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
                offer.locales[locale].name : offer.locales['DEFAULT']?.name
        item.offerName = name
        item.offerDescription = description
        if (order.paymentDescription == null || order.paymentDescription == '') {
            order.paymentDescription = description
        } else if (description != null) {
            order.paymentDescription += ' & ' + description
        }
    }
}