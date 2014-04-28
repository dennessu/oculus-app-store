package com.junbo.order.clientproxy.common

import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.common.id.UserId
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
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
    public static final String ISO8601 = 'yyyy-MM-dd\'T\'hh:mm:ssXXX'

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    def ret = new SimpleDateFormat(ISO8601, Locale.US)
                    ret.timeZone = TimeZone.getTimeZone('UTC');
                    return ret
                }
            }

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
        request.coupons = ((String[])coupons?.toArray()) as Set
        request.currency = order.currency
        request.userId = order.user?.value
        request.shippingMethodId = order.shippingMethod
        request.time = DATE_FORMATTER.get().format(order.honoredTime)
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

    static Email buildOrderConfirmationEmail(Order order, User user, List<OfferRevision> offers, EmailTemplate template) {
        Email email = new Email()
        email.userId = (UserId)(user.id)
        email.templateId = template.id
        // TODO: update email address as IDENTITY component
        email.recipients = [user.username]
        Map<String, String> properties = [:]
        properties.put(ORDER_NUMBER, order.id.value.toString())
        Date now = new Date()
        properties.put(ORDER_DATE, DATE_FORMATTER.get().format(now))
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
            String offerName = offer.locales[template.locale] != null ? offer.locales[template.locale].name :
                    (offer.locales['DEFAULT'] != null ? offer.locales['DEFAULT'].name : '')
            properties.put(OFFER_NAME + index, offerName)
            order.orderItems.each { OrderItem item ->
                if (item.offer.value == offer.offerId) {
                    properties.put(QUANTITY + index, item.quantity.toString())
                    properties.put(PRICE + index, (item.quantity * item.unitPrice).toString())
                }
            }
        }

        email.replacements = properties
        return email
    }
}
