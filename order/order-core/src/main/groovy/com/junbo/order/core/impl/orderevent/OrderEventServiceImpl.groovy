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
}
