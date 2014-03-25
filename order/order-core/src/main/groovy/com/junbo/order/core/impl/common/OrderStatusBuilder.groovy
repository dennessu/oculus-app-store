package com.junbo.order.core.impl.common

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

/**
 * Created by chriszhu on 3/18/14.
 */
@CompileStatic
@Component('orderStatusBuilder')
class OrderStatusBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatusBuilder)

    @Autowired
    OrderRepository orderRepository

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

        if (isOrderFulfilled(orderEvents)) { return OrderStatus.FULFILLED }

        if (isOrderPendingFulfill(orderEvents)) { return OrderStatus.PENDING_FULFILL }

        if (isOrderCharged(orderEvents)) { return OrderStatus.CHARGED }

        if (isOrderPendingCharge(orderEvents)) { return OrderStatus.PENDING_CHARGE }

        if (isOrderPreordered(orderEvents)) { return OrderStatus.PREORDERED }

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

    static Boolean isOrderPendingCharge(List<OrderEvent> orderEvents) {
        if (orderEvents == null) {
            return false
        }
        def orderEvent = orderEvents.find { OrderEvent oe ->
            oe.action == OrderActionType.CHARGE.toString() &&
                    (oe.status == EventStatus.PENDING ||
                    oe.status == EventStatus.PROCESSING)
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
                    (oe.status == EventStatus.PENDING ||
                            oe.status == EventStatus.PROCESSING)
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

    static EventStatus buildEventStatusFromBalance(String balanceStatus) {
        switch (balanceStatus) {
            case BalanceStatus.COMPLETED:
                return EventStatus.COMPLETED
            case BalanceStatus.AWAITING_PAYMENT:
            case BalanceStatus.UNCONFIRMED:
            case BalanceStatus.INIT:
                return EventStatus.PENDING
            case BalanceStatus.PENDING_CAPTURE:
                return EventStatus.OPEN
            case BalanceStatus.CANCELLED:
            case BalanceStatus.FAILED:
            case BalanceStatus.ERROR:
                return EventStatus.FAILED
            default:
                LOGGER.warn('name=Unknown_Balance_Status, status={}', balanceStatus)
                return EventStatus.PENDING
        }
    }

    private static void sortOrderEventsReversely(List<OrderEvent> orderEvents) {
        orderEvents.sort { OrderEvent oe ->
            oe.createdTime
        }.reverse(true)
    }
}
