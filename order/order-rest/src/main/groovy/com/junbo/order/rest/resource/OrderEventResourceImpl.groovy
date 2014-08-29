package com.junbo.order.rest.resource
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrderEventId
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.auth.OrderAuthorizeCallbackFactory
import com.junbo.order.core.OrderEventService
import com.junbo.order.core.OrderService
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.order.spec.resource.OrderEventResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.PathParam
/**
 * Created by chriszhu on 3/12/14.
 */
@CompileStatic
@TypeChecked
@Component('defaultOrderEventResource')
class OrderEventResourceImpl implements OrderEventResource {
    @Resource
    OrderEventService orderEventService

    @Resource
    OrderService orderService

    @Resource
    OrderValidator orderValidator

    @Resource
    AuthorizeService authorizeService

    @Resource
    OrderAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<Results<OrderEvent>> getOrderEvents(OrderId orderId, PageParam pageParam) {
        def callback = authorizeCallbackFactory.create(orderId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            Results<OrderEvent> results = new Results<>()
            if (!AuthorizeContext.hasRights('read-event')) {
                results.setItems([])
                return Promise.pure(results)
            }

            return orderEventService.getOrderEvents(orderId.value, pageParam).then { List<OrderEvent> orderEvents ->
                results.setItems(orderEvents)
                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<OrderEvent> createOrderEvent(OrderEvent orderEvent) {
        orderValidator.notNull(orderEvent, 'orderEvent').notNull(orderEvent.order, 'orderId')

        def callback = authorizeCallbackFactory.create(orderEvent.order as OrderId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create-event') &&
                    !((orderEvent.action == OrderActionType.CHARGE.name() ||
                        orderEvent.action == OrderActionType.FULFILL.name()) &&
                            (orderEvent.status == EventStatus.COMPLETED.name() ||
                                    orderEvent.status == EventStatus.FAILED.name()))) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            def context = new OrderServiceContext()
            return orderService.updateOrderByOrderEvent(orderEvent, context).then { OrderEvent event ->
                return orderEventService.recordEventHistory(event, context)
            }
        }
    }
}
