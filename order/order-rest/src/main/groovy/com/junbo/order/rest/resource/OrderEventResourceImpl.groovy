package com.junbo.order.rest.resource
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderEventService
import com.junbo.order.core.OrderService
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.spec.model.ApiContext
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.resource.OrderEventResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.container.ContainerRequestContext
/**
 * Created by chriszhu on 3/12/14.
 */
@CompileStatic
@TypeChecked
@Scope('prototype')
@Component('defaultOrderEventResource')
class OrderEventResourceImpl implements OrderEventResource {
    @Autowired
    private ContainerRequestContext requestContext

    @Resource
    OrderEventService orderEventService

    @Resource
    OrderService orderService

    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventResourceImpl)

    @Override
    Promise<Results<OrderEvent>> getOrderEvents(OrderId orderId, PageParam pageParam) {
        return orderEventService.getOrderEvents(orderId.value, pageParam).then { List<OrderEvent> orderEvents ->
            Results<OrderEvent> results = new Results<>()
            results.setItems(orderEvents)
            return Promise.pure(results)
        }
    }

    @Override
    Promise<OrderEvent> createOrderEvent(OrderEvent orderEvent) {
        orderValidator.notNull(orderEvent, 'orderEvent').notNull(orderEvent.trackingUuid, 'trackingUuid')
                .notNull(orderEvent.order, 'orderId')
        def persistedOrderEvent = orderEventService.getOrderEventByTrackingUuid(orderEvent.trackingUuid,
                orderEvent.order.value)
        if (persistedOrderEvent != null) {
            LOGGER.info('name=Order_Event_Same_TrackingUuid_Existed')
            return Promise.pure(persistedOrderEvent)
        }
        if (orderEvent.action == OrderActionType.FULFILL.name() && orderEvent.status == OrderStatus.COMPLETED.name()) {
            return orderService.completeChargeOrder(
                    orderEvent.order.value, new ApiContext(requestContext.headers)).then {
                return Promise.pure(orderEventService.createOrderEvent(orderEvent))
            }
        }

        return Promise.pure(orderEventService.createOrderEvent(orderEvent))
    }
}
