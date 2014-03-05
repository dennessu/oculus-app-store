package com.junbo.order.core.common

import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.order.spec.model.ItemType
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.OrderType
import com.junbo.order.spec.model.ShippingInfo
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 2/14/14.
 */
@CompileStatic
class TestBuilder {

    static long generateLong() {
        sleep(1)
        return System.currentTimeMillis()
    }

    static UUID generateUUID() {
        return UUID.randomUUID()
    }

    static Order buildOrderRequest() {
        def order = new Order()
        order.setOrderItems([buildOrderItem()])
        order.setType(OrderType.PAY_IN.toString())
        order.setCountry('US')
        order.setCurrency('USD')
        def userId = new UserId()
        userId.setValue(generateLong())
        order.setUser(userId)
        order.setPaymentInstruments([])
        order.paymentInstruments.add(new PaymentInstrumentId(generateLong()))
        order.setShippingAddressId(generateLong())
        order.setShippingMethodId(generateLong())
        order.setTentative(true)
        order.setTrackingUuid(generateUUID())

        return order
    }

    static OrderItem buildOrderItem() {
        def orderItem = new OrderItem()
        orderItem.setType(ItemType.DIGITAL.toString())
        orderItem.setOffer(generateLong())
        orderItem.setOfferRevision('1')
        return orderItem
    }
}
