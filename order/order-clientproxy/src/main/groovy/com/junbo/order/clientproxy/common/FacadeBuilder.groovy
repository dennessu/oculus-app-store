package com.junbo.order.clientproxy.common
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.common.id.UserId
import com.junbo.email.spec.model.Email
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.v1.model.User
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.rating.spec.model.request.RatingItem
import com.junbo.rating.spec.model.request.RatingRequest
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import java.text.SimpleDateFormat
/**
 * Created by LinYi on 14-3-4.
 */
@CompileStatic
@TypeChecked
class FacadeBuilder {

    public static final String ORDER_NUMBER = 'ORDERNUMBER:OrderId'
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
        request.shippingMethodId = order?.shippingMethod
        request.shippingAddressId = order?.shippingAddress?.value
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

    static RatingRequest buildRatingRequest(Order order) {
        RatingRequest request = new RatingRequest()
        List<String> coupons = []
        order.discounts?.each { Discount d ->
            if (!d.coupon?.isEmpty()) {
                coupons.add(d.coupon)
            }
        }
        request.couponCodes = ((String[])coupons?.toArray()) as Set
        request.currency = order.currency
        request.userId = order.user?.value
        request.shippingMethodId = order.shippingMethod
        request.timestamp = order.honoredTime.time
        request.includeCrossOfferPromos = true
        List<RatingItem> ratingItems = []
        order.orderItems?.each { OrderItem item ->
            RatingItem ratingItem = new RatingItem()
            ratingItem.offerId = item.offer.value
            ratingItem.quantity = item.quantity
            ratingItems.add(ratingItem)
        }
        request.lineItems = ((RatingItem[])ratingItems.toArray()) as Set
        return request
    }

    static Email buildOrderConfirmationEmail(Order order, User user, List<OfferRevision> offers) {
        Email email = new Email()
        email.userId = (UserId)(user.id)
        email.source = 'SilkCloud'
        email.action = 'OrderConfirmation'
        email.locale = 'en_US'
        // TODO: update email address as IDENTITY component
        email.recipient = user.username
        Map<String, String> properties = [:]
        properties.put(ORDER_NUMBER, order.id.value.toString())
        Date now = new Date()
        properties.put(ORDER_DATE, new SimpleDateFormat('yyyy-MM-dd', Locale.US).format(now))
        properties.put(NAME, user.username)
        properties.put(SUBTOTAL, order.totalAmount?.toString())
        properties.put(TAX, BigDecimal.ZERO.toString())
        def grandTotal = order.totalAmount
        if (order.totalTax != null) {
            grandTotal += order.totalTax
            properties.put(TAX, order.totalTax.toString())
        }
        properties.put(GRAND_TOTAL, grandTotal.toString())
        offers.eachWithIndex { OfferRevision offer, int index ->
            // TODO update the l10n logic per catalog change
            properties.put(OFFER_NAME + index, offer.name.locales['DEFAULT'])
            order.orderItems.each { OrderItem item ->
                if (item.offer.value == offer.offerId) {
                    properties.put(QUANTITY + index, item.quantity.toString())
                    properties.put(PRICE + index, (item.quantity * item.unitPrice).toString())
                }
            }
        }
        email.properties = properties
        return email
    }
}
