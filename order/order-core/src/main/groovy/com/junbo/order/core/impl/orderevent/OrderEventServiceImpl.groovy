package com.junbo.order.core.impl.orderevent

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderEventService
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-20.
 */
@CompileStatic
@Service('orderEventService')
class OrderEventServiceImpl implements OrderEventService {
    @Resource
    OrderRepository orderRepository

    @Override
    @Transactional
    Promise<List<OrderEvent>> getOrderEvents(Long orderId) {
        if (orderId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('orderId', 'orderId cannot be null').exception()
        }

        def orderEvents = orderRepository.getOrderEvents(orderId)
        if (orderEvents == null || orderEvents.size() == 0) {
            throw AppErrors.INSTANCE.orderEventNotFound().exception()
        }

        return Promise.pure(orderEvents)
    }
}
