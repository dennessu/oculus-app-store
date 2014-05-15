package com.junbo.order.core.impl.common

import com.junbo.catalog.spec.enums.ItemType
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.FulfillmentAction
import com.junbo.order.spec.model.FulfillmentHistory
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Util class for building fulfillment history.
 */
@CompileStatic
class FulfillmentEventHistoryBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfillmentEventHistoryBuilder)

    static FulfillmentHistory buildFulfillmentHistory(FulfilmentRequest fulfilmentResult,
                                                           FulfilmentItem fulfilmentItem, OrderItem orderItem) {
        def fulfillmentHistory = new FulfillmentHistory()
        fulfillmentHistory.trackingUuid = UUID.fromString(fulfilmentResult.trackingGuid)
        fulfillmentHistory.fulfillmentEvent = getFulfillmentEvent(fulfilmentItem, orderItem)
        fulfillmentHistory.orderItemId = fulfilmentItem.orderItemId
        fulfillmentHistory.fulfillmentId = fulfilmentItem.fulfilmentId
        return fulfillmentHistory
    }

    static FulfillmentHistory buildFulfillmentHistory(FulfilmentRequest fulfilmentResult,
                                                      FulfilmentItem fulfilmentItem, OrderEvent event) {
        def fulfillmentHistory = new FulfillmentHistory()
        fulfillmentHistory.trackingUuid = UUID.fromString(fulfilmentResult.trackingGuid)
        fulfillmentHistory.fulfillmentEvent = getFulfillmentEvent(fulfilmentItem, event)
        fulfillmentHistory.orderItemId = fulfilmentItem.orderItemId
        fulfillmentHistory.fulfillmentId = fulfilmentItem.fulfilmentId
        return fulfillmentHistory
    }

    static String getFulfillmentEvent(FulfilmentItem fulfilmentItem, OrderItem orderItem) {
        if (orderItem == null) {
            return null
        }
        switch (fulfilmentItem.status) {
            case FulfilmentStatus.PENDING:
                if (orderItem.type == ItemType.PHYSICAL.name()) {
                    return FulfillmentAction.PREORDER.name()
                }
                return null
            case FulfilmentStatus.SUCCEED:
                if (orderItem.type == ItemType.PHYSICAL.name()) {
                    return FulfillmentAction.SHIP.name()
                }
                if (orderItem.type == ItemType.DIGITAL.name()) {
                    return FulfillmentAction.FULFILL.name()
                }
                return null
        }
        return null
    }

    static String getFulfillmentEvent(FulfilmentItem fulfilmentItem, OrderEvent event) {
        if (event == null) {
            return null
        }
        if (event.status == EventStatus.COMPLETED.name()
                && fulfilmentItem.status == FulfilmentStatus.SUCCEED.toString()) {
            return FulfillmentAction.SHIP.name()
        }
        return null
    }

    static EventStatus getFulfillmentEventStatus(FulfilmentItem fulfilmentItem) {
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
