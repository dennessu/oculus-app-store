package com.junbo.order.core.impl.common

import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.clientproxy.model.OrderOfferItem
import com.junbo.order.db.entity.enums.ItemType
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 3/19/14.
 */
@CompileStatic
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

}
