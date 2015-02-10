package com.junbo.order.core.impl.common
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.enums.OrderStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 3/18/14.
 */
@CompileStatic
@TypeChecked
@Component('orderStatusBuilder')
class OrderStatusBuilder {

    static OrderStatus buildOrderStatus(Order order) {
        if (order.tentative) {
            return OrderStatus.OPEN
        }

        if (CoreUtils.isCanceled(order)) { return OrderStatus.CANCELED }

        if (CoreUtils.isRefunded(order)) { return OrderStatus.REFUNDED }

        if (CoreUtils.isChargeBack(order)) { return OrderStatus.CHARGE_BACK }

        if (CoreUtils.isFulfillCompleted(order) && CoreUtils.isChargeCompleted(order)) {
            return OrderStatus.COMPLETED
        }

        if (CoreUtils.isShipCompleted(order) && CoreUtils.isChargeCompleted(order)) {
            return OrderStatus.SHIPPED
        }

        if (CoreUtils.isFulfillCompleted(order)) { return OrderStatus.PENDING }

        if (CoreUtils.isChargeCompleted(order)) { return OrderStatus.PENDING }

        if (CoreUtils.isPendingOnFulfillment(order)) { return OrderStatus.PENDING }

        if (CoreUtils.isPendingOnChargeConfirmation(order)) { return OrderStatus.PENDING }

        if (CoreUtils.isPendingOnCapture(order)) { return OrderStatus.PENDING }

        if (CoreUtils.isPreordered(order)) { return OrderStatus.PREORDERED }

        return OrderStatus.OPEN
    }
}
