package com.junbo.order.core.impl.orderaction

import com.junbo.common.id.OrderItemId
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.FulfillmentEvent
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
class  FulfillmentAction implements Action {

    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfillmentAction)

    private static final Map<EventStatus, Integer> ITEMSTATUSPRIORITY = [
            (EventStatus.COMPLETED) : 0,
            (EventStatus.PENDING) : 1,
            (EventStatus.FAILED) : 2
        ]

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext
        def order = serviceContext.order

        facadeContainer.fulfillmentFacade.postFulfillment(order).syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_FulfillmentAction_Error', throwable)
            return null
        }.syncThen { FulfilmentRequest fulfilmentResult ->
            if (fulfilmentResult == null) { // error in post fulfillment
                orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(
                                order.id,
                                OrderActionType.FULFILL,
                                EventStatus.ERROR,
                                ActionUtils.getFlowType(actionContext),
                                context.trackingUuid))
            } else {
                EventStatus orderEventStatus = EventStatus.COMPLETED
                fulfilmentResult.items.each { FulfilmentItem fulfilmentItem ->
                    def fulfillmentEvent = toFulfillmentEvent(fulfilmentResult, fulfilmentItem)
                    def fulfillmentEventStatus = EventStatus.valueOf(fulfillmentEvent.status)
                    // aggregate fulfillment event status to update order event status
                    if (orderEventStatus == null ||
                            ITEMSTATUSPRIORITY[fulfillmentEventStatus] > ITEMSTATUSPRIORITY[orderEventStatus]) {
                        orderEventStatus = fulfillmentEventStatus
                    }
                    orderRepository.createFulfillmentEvent(order.id.value, fulfillmentEvent)
                }
                orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(
                                order.id,
                                OrderActionType.FULFILL, orderEventStatus,
                                ActionUtils.getFlowType(actionContext),
                                context.trackingUuid))
            }
            return ActionUtils.DEFAULT_RESULT
        }
    }

    private static FulfillmentEvent toFulfillmentEvent(FulfilmentRequest fulfilmentResult,
                                                       FulfilmentItem fulfilmentItem) {
        def fulfillmentEvent = new FulfillmentEvent()
        fulfillmentEvent.trackingUuid = UUID.fromString(fulfilmentResult.trackingGuid)
        fulfillmentEvent.action = com.junbo.order.db.entity.enums.FulfillmentAction.FULFILL.toString()
        fulfillmentEvent.orderItem = new OrderItemId(fulfilmentItem.orderItemId)
        fulfillmentEvent.status = getFulfillmentEventStatus(fulfilmentItem).name()
        fulfillmentEvent.fulfillmentId = fulfilmentItem.fulfilmentId
        return fulfillmentEvent
    }

    private static EventStatus getFulfillmentEventStatus(FulfilmentItem fulfilmentItem) {
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
        return null
    }
}
