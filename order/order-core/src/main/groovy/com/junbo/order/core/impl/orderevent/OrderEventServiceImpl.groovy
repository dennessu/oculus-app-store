package com.junbo.order.core.impl.orderevent

import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.OrderEventService
import com.junbo.order.core.impl.common.BillingEventHistoryBuilder
import com.junbo.order.core.impl.common.FulfillmentEventHistoryBuilder
import com.junbo.order.core.impl.common.ParamUtils
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.enums.OrderActionType
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by LinYi on 14-3-20.
 */
@CompileStatic
@TypeChecked
@Service('orderEventService')
class OrderEventServiceImpl implements OrderEventService {
    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventServiceImpl)

    @Override
    @Transactional
    Promise<List<OrderEvent>> getOrderEvents(Long orderId, PageParam pageParam) {
        if (orderId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('orderId', 'orderId cannot be null').exception()
        }

        if (orderRepository.getOrder(orderId) == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }

        def orderEvents = orderRepository.getOrderEvents(orderId, ParamUtils.processPageParam(pageParam))

        return Promise.pure(orderEvents)
    }

    @Override
    @Transactional
    Promise<OrderEvent> recordEventHistory(OrderEvent event, OrderServiceContext orderServiceContext) {
        switch (event.action) {
            case OrderActionType.FULFILL.name():
                return recordFulfillHistory(event, orderServiceContext)

            case OrderActionType.CHARGE.name():
            case OrderActionType.PARTIAL_CHARGE.name():
            case OrderActionType.AUTHORIZE.name():
            case OrderActionType.CAPTURE.name():
            case OrderActionType.REFUND.name():
            case OrderActionType.PARTIAL_REFUND.name():
                return recordBillingHistory(event, orderServiceContext)
            default:
                return Promise.pure(event)
        }
    }

    private Promise<OrderEvent> recordFulfillHistory(OrderEvent event, OrderServiceContext context) {
        return builder.getFulfillmentRequest(context).then { FulfilmentRequest fulfillment ->
            if (fulfillment == null) {
                LOGGER.error('name=Order_GetFulfillment_Error_Fulfillment_Null')
                throw AppErrors.INSTANCE.
                        fulfillmentConnectionError('get fulfillment returns null').exception()
            }
            fulfillment.items.each { FulfilmentItem fulfilmentItem ->
                def fulfillmentHistory = FulfillmentEventHistoryBuilder.buildFulfillmentHistory(
                        fulfillment, fulfilmentItem, event)
                if (fulfillmentHistory.fulfillmentEvent != null) {
                    orderRepository.createFulfillmentHistory(fulfillmentHistory)
                }
            }
            return Promise.pure(event)
        }
    }

    @Override
    @Transactional
    Promise<OrderEvent> recordBillingHistory(OrderEvent event, OrderServiceContext orderServiceContext) {
        if (event?.billingInfo?.balance != null) {
            def balance = Promise.get { builder.getOrderEventBalance(orderServiceContext) }
            if (balance == null) {
                throw AppErrors.INSTANCE.balanceNotFound().exception()
            }

            def billingHistory = BillingEventHistoryBuilder.buildBillingHistory(balance)
            if (billingHistory.billingEvent != null) {
                orderRepository.createBillingHistory(event.order.value, billingHistory)
            }
        }
        return Promise.pure(event)
    }
}
