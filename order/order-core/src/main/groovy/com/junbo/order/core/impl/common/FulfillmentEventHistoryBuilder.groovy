
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.order.spec.model.FulfillmentHistory
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.FulfillmentEventType
import com.junbo.order.spec.model.enums.ItemType
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
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
        fulfillmentHistory.trackingUuid = UUID.fromString(fulfilmentResult.trackingUuid)
        fulfillmentHistory.fulfillmentEvent = getFulfillmentEvent(fulfilmentItem, orderItem)
        fulfillmentHistory.orderItemId = fulfilmentItem.itemReferenceId
        fulfillmentHistory.fulfillmentId = fulfilmentItem.fulfilmentId

        fulfillmentHistory.success = true
        fulfillmentHistory.success = !fulfilmentItem.actions?.any { FulfilmentAction fa ->
            fa.status == FulfilmentStatus.FAILED || fa.status == FulfilmentStatus.UNKNOWN
        }
        if (fulfilmentItem.actions?.any {
            FulfilmentAction fa ->
                fa.status == FulfilmentStatus.PENDING
        }) {
            if (orderItem.isPreorder) {
                fulfillmentHistory.fulfillmentEvent = FulfillmentEventType.PREORDER.name()
            } else if (orderItem.type == ItemType.PHYSICAL_GOODS.name()){
                fulfillmentHistory.fulfillmentEvent = FulfillmentEventType.REQUEST_SHIP.name()
            } else {
                fulfillmentHistory.fulfillmentEvent = FulfillmentEventType.REQUEST_FULFILL.name()
            }
        }
        return fulfillmentHistory
    }

    static FulfillmentHistory buildFulfillmentHistory(FulfilmentRequest fulfilmentResult,
                                                      FulfilmentItem fulfilmentItem, OrderEvent event) {
        def fulfillmentHistory = new FulfillmentHistory()
        fulfillmentHistory.trackingUuid = UUID.fromString(fulfilmentResult.trackingUuid)
        fulfillmentHistory.fulfillmentEvent = getFulfillmentEvent(fulfilmentItem, event)
        fulfillmentHistory.orderItemId = fulfilmentItem.itemReferenceId
        fulfillmentHistory.fulfillmentId = fulfilmentItem.fulfilmentId

        fulfillmentHistory.success = true
        fulfillmentHistory.success = !fulfilmentItem.actions?.any { FulfilmentAction fa ->
            fa.status == FulfilmentStatus.FAILED || fa.status == FulfilmentStatus.UNKNOWN
        }
        if (fulfilmentItem.actions?.any {
            FulfilmentAction fa ->
                fa.status == FulfilmentStatus.PENDING
        }) {
            fulfillmentHistory.fulfillmentEvent = FulfillmentEventType.REQUEST_SHIP
        }
        return fulfillmentHistory
    }

    static String getFulfillmentEvent(FulfilmentItem fulfilmentItem, OrderItem orderItem) {
        if (orderItem == null) {
            return null
        }
        if (orderItem.isPreorder) {
            return FulfillmentEventType.PREORDER.name()
        }
        return FulfillmentEventType.FULFILL.name()
    }

    static String getFulfillmentEvent(FulfilmentItem fulfilmentItem, OrderEvent event) {
        if (event == null) {
            return null
        }
        return event.action
    }

    static EventStatus getFulfillmentEventStatus(FulfilmentItem fulfilmentItem) {

        if (CollectionUtils.isEmpty(fulfilmentItem.actions)) {
            return EventStatus.COMPLETED
        }

        if (fulfilmentItem.actions.any { FulfilmentAction fa ->
            fa.status == FulfilmentStatus.UNKNOWN
        }) {
            return EventStatus.ERROR
        }

        if (fulfilmentItem.actions.any { FulfilmentAction fa ->
            fa.status == FulfilmentStatus.FAILED
        }) {
            return EventStatus.FAILED
        }

        if (fulfilmentItem.actions.any { FulfilmentAction fa ->
            fa.status == FulfilmentStatus.PENDING
        }) {
            return EventStatus.PENDING
        }

        if (fulfilmentItem.actions.any { FulfilmentAction fa ->
            fa.status == FulfilmentStatus.SUCCEED
        }) {
            return EventStatus.COMPLETED
        }

        LOGGER.warn('name=Unknown_Fulfillment_Status, fulfilmentId={}, orderItemId={}',
                fulfilmentItem.fulfilmentId.toString(), fulfilmentItem.itemReferenceId.toString())
        return EventStatus.ERROR
    }
}
