package com.junbo.order.test.util

import com.junbo.common.id.OfferId
import com.junbo.order.spec.model.OrderItem
import org.springframework.stereotype.Component

/**
 * Created by fzhang on 14-3-17.
 */
@Component('OrderTestGenerator')
class Generator {

    OrderItem generateOrderItem(String type, OfferId offerId, int quantity) {
        def item = new OrderItem()
        item.type = type
        item.offer = offerId
        item.quantity = quantity
        return item
    }
}
