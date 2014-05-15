package com.junbo.order.core.impl.orderevent

import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.OrderEventService
import com.junbo.order.core.impl.common.FulfillmentEventHistoryBuilder
import com.junbo.order.core.impl.common.ParamUtils
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
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
    @Resource
    OrderRepository orderRepository

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

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
    Promise<OrderEvent> recordEventHistory(OrderEvent event) {
        switch (event.action) {
            case OrderActionType.FULFILL.name():
                return facadeContainer.fulfillmentFacade.getFulfillment(event.order)
                        .then { FulfilmentRequest fulfillment ->
                    fulfillment.items.each { FulfilmentItem fulfilmentItem ->
                        def fulfillmentHistory = FulfillmentEventHistoryBuilder.buildFulfillmentHistory(
                            fulfillment, fulfilmentItem, event)
                        if (fulfillmentHistory.fulfillmentEvent != null) {
                            orderRepository.createFulfillmentHistory(event.order.value, fulfillmentHistory)
                        }
                    }
                    return Promise.pure(event)
                }
            default:
                return Promise.pure(event)
        }
    }
}
