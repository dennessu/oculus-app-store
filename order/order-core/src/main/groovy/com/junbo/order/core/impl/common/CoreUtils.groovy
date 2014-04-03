package com.junbo.order.core.impl.common
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.clientproxy.model.OrderOfferItem
import com.junbo.order.db.entity.enums.ItemType
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils

/**
 * Created by chriszhu on 3/19/14.
 */
@CompileStatic
@TypeChecked
class CoreUtils {

    static final String OFFER_ITEM_TYPE_PHYSICAL = 'PHYSICAL'

    static ItemType getOfferType(OrderOffer offer) {
        // TODO support bundle type
        Boolean hasPhysical = offer.orderOfferItems?.any { OrderOfferItem item ->
            item.catalogItem.type == OFFER_ITEM_TYPE_PHYSICAL
        }
        if (hasPhysical) { return ItemType.PHYSICAL }
        return ItemType.DIGITAL
    }

    static AppError[] toAppErrors(Throwable throwable) {
        if (throwable instanceof AppErrorException) {
            return [throwable.error] as AppError[]
        }
        return new AppError[0]
    }

    static Boolean isFreeOrder(Order order) {
        return order.totalAmount == BigDecimal.ZERO
    }

    static Boolean hasPhysicalOffer(Order order) {
        if (CollectionUtils.isEmpty(order.orderItems)) {
            return false
        }
        if (order.orderItems.any { OrderItem oi ->
            oi.type == ItemType.PHYSICAL.toString()
        }) {
            return true
        }
        return false
    }

    static Boolean isRateExpired(Order order) {
        if (order.honorUntilTime == null) {
            return true
        }
        if (order.honorUntilTime > new Date()) {
            return true
        }
        return false
    }
}
