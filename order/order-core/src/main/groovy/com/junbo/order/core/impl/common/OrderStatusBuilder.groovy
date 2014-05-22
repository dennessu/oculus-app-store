package com.junbo.order.core.impl.common

import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

/**
 * Created by chriszhu on 3/18/14.
 */
@CompileStatic
@TypeChecked
@Component('orderStatusBuilder')
class OrderStatusBuilder {

    static OrderStatus buildOrderStatus(Order order, List<OrderEvent> orderEvents) {
        if (order.tentative) {
            return OrderStatus.OPEN
        }
        if (CollectionUtils.isEmpty(orderEvents)) {
            return OrderStatus.OPEN
        }
        sortOrderEventsReversely(orderEvents)

        if (isOrderCancelled(orderEvents)) { return OrderStatus.CANCELED }

        if (isOrderRefunded(orderEvents)) { return OrderStatus.REFUNDED }

        if (isOrderFailed(orderEvents)) { return OrderStatus.FAILED }

        if (isOrderFulfilled(orderEvents) && isOrderCharged(orderEvents)) { return OrderStatus.COMPLETED }

        if (isOrderFulfilled(orderEvents) && CoreUtils.isFreeOrder(order)) { return OrderStatus.COMPLETED }

        if (isOrderFulfilled(orderEvents)) { return OrderStatus.FULFILLED }

        if (isOrderPendingFulfill(orderEvents)) { return OrderStatus.PENDING_FULFILL }

        if (isOrderCharged(orderEvents)) { return OrderStatus.CHARGED }

        if (isOrderPendingCharge(orderEvents)) { return OrderStatus.PENDING_CHARGE }

        if (isOrderPreordered(orderEvents)) { return OrderStatus.PREORDERED }

        if (isOrderPartialCharged(orderEvents)) { return OrderStatus.PENDING_FULFILL }

        return OrderStatus.OPEN
    }

    static Boolean isOrderFailed(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            (oe.action == OrderActionType.AUTHORIZE.toString() && oe.status == EventStatus.FAILED.toString()) ||
                    (oe.action == OrderActionType.CHARGE.toString() && oe.status == EventStatus.FAILED.toString()) ||
                    (oe.action == OrderActionType.FULFILL.toString() && oe.status == EventStatus.FAILED.toString())
        }
        return orderEvent != null
    }

    static Boolean isOrderPreordered(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.PREORDER.toString() &&
                    oe.status == EventStatus.COMPLETED.toString()
        }
        return orderEvent != null
    }

    static Boolean isOrderCharged(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.CHARGE.toString() &&
                    oe.status == EventStatus.COMPLETED.toString()
        }
        return orderEvent != null
    }

    static Boolean isOrderPartialCharged(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.PARTIAL_CHARGE.toString() &&
                    oe.status == EventStatus.COMPLETED.toString()
        }
        return orderEvent != null
    }

    static Boolean isOrderPendingCharge(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.CHARGE.toString() &&
                    (oe.status == EventStatus.PENDING.toString() ||
                    oe.status == EventStatus.PROCESSING.toString())
        }
        return orderEvent != null
    }

    static Boolean isOrderFulfilled(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.FULFILL.toString() &&
                    oe.status == EventStatus.COMPLETED.toString()
        }
        return orderEvent != null
    }

    static Boolean isOrderPendingFulfill(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.FULFILL.toString() &&
                    (oe.status == EventStatus.PENDING.toString() ||
                            oe.status == EventStatus.PROCESSING.toString())
        }
        return orderEvent != null
    }

    static Boolean isOrderRefunded(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.REFUND.toString() &&
                    oe.status == EventStatus.COMPLETED.toString()
        }
        return orderEvent != null
    }

    static Boolean isOrderCancelled(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.CANCEL.toString() &&
                    oe.status == EventStatus.COMPLETED.toString()
        }
        return orderEvent != null
    }

    private static void sortOrderEventsReversely(List<OrderEvent> orderEvents) {
        orderEvents.sort { OrderEvent oe ->
            oe.createdTime
        }.reverse(true)
    }

}
