package com.junbo.order.core.impl.orderaction

import com.junbo.catalog.spec.enums.ItemType
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.error.ErrorUtils
import com.junbo.order.spec.model.FulfillmentHistory
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
@TypeChecked
class  FulfillmentAction extends BaseOrderEventAwareAction {

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
    @OrderEventAwareBefore(action = 'FulfillmentAction')
    @OrderEventAwareAfter(action = 'FulfillmentAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext
        def order = serviceContext.order

        return facadeContainer.fulfillmentFacade.postFulfillment(order).syncRecover { Throwable throwable ->
            LOGGER.error('name=Order_FulfillmentAction_Error', throwable)
            throw AppErrors.INSTANCE.
                    fulfilmentConnectionError(ErrorUtils.toAppErrors(throwable)).exception()
        }.syncThen { FulfilmentRequest fulfilmentResult ->
            EventStatus orderEventStatus = EventStatus.COMPLETED

            fulfilmentResult.items.each { FulfilmentItem fulfilmentItem ->
                OrderItem orderItem = order.orderItems?.find { OrderItem item ->
                    item.orderItemId?.value == fulfilmentItem.orderItemId
                }
                def fulfillmentHistory = toFulfillmentHistory(fulfilmentResult, fulfilmentItem, orderItem)
                def fulfillmentEventStatus = getFulfillmentEventStatus(fulfilmentItem)

                // aggregate fulfillment event status to update order event status
                if (orderEventStatus == null ||
                        ITEMSTATUSPRIORITY[fulfillmentEventStatus] > ITEMSTATUSPRIORITY[orderEventStatus]) {
                    orderEventStatus = fulfillmentEventStatus
                }
                if (fulfillmentHistory.fulfillmentEvent != null) {
                    def savedHistory = orderRepository.createFulfillmentHistory(order.id.value, fulfillmentHistory)
                    if (orderItem.fulfillmentHistories == null) {
                        orderItem.fulfillmentHistories = [savedHistory]
                    }
                    else {
                        orderItem.fulfillmentHistories.add(savedHistory)
                    }
                }
            }

            return CoreBuilder.buildActionResultForOrderEventAwareAction(context, orderEventStatus)
        }
    }

    private static FulfillmentHistory toFulfillmentHistory(FulfilmentRequest fulfilmentResult,
                                                       FulfilmentItem fulfilmentItem, OrderItem orderItem) {
        def fulfillmentHistory = new FulfillmentHistory()
        fulfillmentHistory.trackingUuid = UUID.fromString(fulfilmentResult.trackingGuid)
        fulfillmentHistory.fulfillmentEvent = getFulfillmentEvent(fulfilmentItem, orderItem)
        fulfillmentHistory.orderItemId = fulfilmentItem.orderItemId
        fulfillmentHistory.fulfillmentId = fulfilmentItem.fulfilmentId
        return fulfillmentHistory
    }

    private static String getFulfillmentEvent(FulfilmentItem fulfilmentItem, OrderItem orderItem) {
        if (orderItem == null) {
            return null
        }
        switch (fulfilmentItem.status) {
            case FulfilmentStatus.PENDING:
                if (orderItem.type == ItemType.PHYSICAL.name()) {
                    return com.junbo.order.db.entity.enums.FulfillmentAction.PREORDER.name()
                }
                return null
            case FulfilmentStatus.SUCCEED:
                if (orderItem.type == ItemType.PHYSICAL.name()) {
                    return com.junbo.order.db.entity.enums.FulfillmentAction.SHIP.name()
                }
                if (orderItem.type == ItemType.DIGITAL.name()) {
                    return com.junbo.order.db.entity.enums.FulfillmentAction.FULFILL.name()
                }
                return null
        }
        return null
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
                fulfilmentItem.fulfilmentId.toString(), fulfilmentItem.orderItemId.toString(), fulfilmentItem.status)
        return null
    }
}
