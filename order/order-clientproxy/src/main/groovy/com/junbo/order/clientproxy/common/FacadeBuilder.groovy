package com.junbo.order.clientproxy.common

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.email.spec.model.Email
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.model.user.User
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.rating.spec.model.request.OrderRatingItem
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic

/**
 * Created by LinYi on 14-3-4.
 */
@CompileStatic
class FacadeBuilder {

    public static final String ORDER_NUMBER = 'ORDERNUMBER'
    public static final String ORDER_DATE = 'ORDERDATE'
    public static final String NAME = 'NAME'
    public static final String OFFER_NAME = 'OFFERNAME'
    public static final String QUANTITY = 'QTY'
    public static final String PRICE = 'PRICE'
    public static final String SUBTOTAL = 'SUBTOTAL'
    public static final String TAX = 'TAX'
    public static final String GRAND_TOTAL = 'GRANDTOTAL'

    static FulfilmentRequest buildFulfilmentRequest(Order order) {
        FulfilmentRequest request = new FulfilmentRequest()
        request.userId = order.user.value
        request.orderId = order.id.value
        request.trackingGuid = UUID.randomUUID()
        request.shippingMethodId = order?.shippingMethodId
        request.shippingAddressId = order?.shippingAddressId?.value
        request.items = []
        order.orderItems?.each { OrderItem item ->
            request.items << buildFulfilmentItem(item)
        }
        return request
    }

    private static FulfilmentItem buildFulfilmentItem(OrderItem orderItem) {
        FulfilmentItem item = new FulfilmentItem()
        item.orderItemId = orderItem.orderItemId.value
        item.offerId = orderItem.offer.value
        item.timestamp = orderItem.honoredTime?.time
        item.quantity = orderItem.quantity
        return item
    }

    static OrderRatingRequest buildOrderRatingRequest(Order order) {
        OrderRatingRequest request = new OrderRatingRequest()
        request.country = order.country
        List<String> coupons = []
        order.discounts?.each { Discount d ->
            if (!d.coupon?.isEmpty()) {
                coupons.add(d.coupon)
            }
        }
        request.couponCodes = ((String[])coupons?.toArray()) as Set
        request.currency = order.currency
        request.userId = order.user?.value
        request.country = order.country
        request.shippingMethodId = order.shippingMethodId
        List<OrderRatingItem> ratingItems = []
        order.orderItems?.each { OrderItem item ->
            OrderRatingItem ratingItem = new OrderRatingItem()
            ratingItem.offerId = item.offer.value
            ratingItem.quantity = item.quantity
            ratingItems.add(ratingItem)
        }
        request.lineItems = ((OrderRatingItem[])ratingItems.toArray()) as Set
        return request
    }

    static Email buildOrderConfirmationEmail(Order order, User user, List<Offer> offers) {
        Email email = new Email()
        email.userId = user.id
        email.source = 'SilkCloud'
        email.action = 'OrderConfirmation'
        email.locale = 'en_US'
        // TODO: update email address as IDENTITY component
        email.recipient = user.userName
        Map<String, String> properties = [:]
        properties.put(ORDER_NUMBER, order.id.value.toString())
        properties.put(ORDER_DATE, new Date().toString())
        properties.put(NAME, user.userName)
        properties.put(SUBTOTAL, order.totalAmount?.toString())
        properties.put(TAX, BigDecimal.ZERO.toString())
        def grandTotal = order.totalAmount
        if (order.totalTax != null) {
            grandTotal += order.totalTax
            properties.put(TAX, order.totalTax.toString())
        }
        properties.put(GRAND_TOTAL, grandTotal.toString())
        offers.eachWithIndex { Offer offer, int index ->
            properties.put(OFFER_NAME + index, offer.name)
            order.orderItems.each { OrderItem item ->
                if (item.offer.value == offer.id) {
                    properties.put(QUANTITY + index, item.quantity.toString())
                    properties.put(PRICE + index, (item.quantity * item.unitPrice).toString())
                }
            }
        }
        email.properties = properties
        return email
    }
}
