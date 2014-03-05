package com.junbo.order.core.impl.orderaction

import com.junbo.common.id.OrderItemId
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.orderaction.context.BaseContext
import com.junbo.order.spec.model.EventStatus
import com.junbo.order.spec.model.FulfillmentEvent
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
class FulfillmentAction implements OrderAction<BaseContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfillmentAction)

    private static final Map<EventStatus, Integer> ITEMSTATUSPRIORITY = [
            (EventStatus.COMPLETED) : 0,
            (EventStatus.PENDING) : 1,
            (EventStatus.FAILED) : 2
        ]

    @Override
    Promise<BaseContext> execute(BaseContext request) {
        def serviceContext = request.orderServiceContext
        def order = serviceContext.order

        serviceContext.fulfillmentFacade.postFulfillment(order).syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_FulfillmentAction_Error', throwable)
            return null
        }.syncThen { FulfilmentRequest fulfilmentResult ->
            if (fulfilmentResult == null) { // error in post fulfillment
                serviceContext.orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(order.id,
                                com.junbo.order.spec.model.OrderAction.FULFILL, EventStatus.ERROR))
            } else {
                EventStatus orderEventStatus = null
                fulfilmentResult.items.each { FulfilmentItem fulfilmentItem ->
                    def fulfillmentEvent = toFulfillmentEvent(fulfilmentResult, fulfilmentItem)
                    def fulfillmentEventStatus = EventStatus.valueOf(fulfillmentEvent.status)
                    // aggregate fulfillment event status to update order event status
                    if (orderEventStatus == null ||
                            ITEMSTATUSPRIORITY[fulfillmentEventStatus] > ITEMSTATUSPRIORITY[orderEventStatus]) {
                        orderEventStatus = fulfillmentEventStatus
                    }
                    serviceContext.orderRepository.createFulfillmentEvent(fulfillmentEvent)
                }
                serviceContext.orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(order.id,
                                com.junbo.order.spec.model.OrderAction.FULFILL, orderEventStatus))
            }
            return request
        }
    }

    private FulfillmentEvent toFulfillmentEvent(FulfilmentRequest fulfilmentResult, FulfilmentItem fulfilmentItem) {
        def fulfillmentEvent = new FulfillmentEvent()
        fulfillmentEvent.trackingUuid = UUID.fromString(fulfilmentResult.trackingGuid)
        fulfillmentEvent.fulfillmentId = fulfilmentResult.requestId
        fulfillmentEvent.action = com.junbo.order.spec.model.FulfillmentAction.FULFILL.toString()
        fulfillmentEvent.orderItem = new OrderItemId(fulfilmentItem.orderItemId)
        fulfillmentEvent.status = getFulfillmentEventStatus(fulfilmentItem).name()
        return fulfillmentEvent
    }

    private EventStatus getFulfillmentEventStatus(FulfilmentItem fulfilmentItem) {
        switch (fulfilmentItem.status) {
            case FulfilmentStatus.PENDING:
                return EventStatus.PENDING
            case FulfilmentStatus.SUCCEED:
                return EventStatus.COMPLETED
            case FulfilmentStatus.FAILED:
                return EventStatus.FAILED
        }
        LOGGER.warn('name=Unknown_Fulfillment_Status, fulfilmentId={}, orderItemId={}, status={}',
                [fulfilmentItem.fulfilmentId.toString(), fulfilmentItem.orderItemId.toString(), fulfilmentItem.status])
        return EventStatus.PROCESSING
    }
}
