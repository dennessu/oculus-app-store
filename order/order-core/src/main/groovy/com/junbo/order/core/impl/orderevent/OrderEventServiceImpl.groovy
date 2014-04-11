package com.junbo.order.core.impl.orderevent

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderEventService
import com.junbo.order.core.impl.common.ParamUtils
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
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
    @Resource
    OrderRepository orderRepository

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
    OrderEvent getOrderEventByTrackingUuid(UUID trackingUuid, Long orderId) {
        if (trackingUuid == null) {
            return null
        }
        def orderEvent = orderRepository.getOrderEventByTrackingUuid(trackingUuid)
        if (orderEvent != null) {
            if (orderEvent.order.value != orderId) {
                LOGGER.error('name=Dup_Tracking_Uuid_Different_Order')
                throw AppErrors.INSTANCE.orderEventDuplicateTrackingGuid(0L, trackingUuid).exception()
            }
        }
        return  orderEvent
    }

    @Override
    @Transactional
    OrderEvent createOrderEvent(OrderEvent orderEvent) {
        LOGGER.info('name=Create_Order_Event. orderId: {}', orderEvent.order.value)

        orderEvent.id = null
        return orderRepository.createOrderEvent(orderEvent)
    }
}
