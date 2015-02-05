package com.junbo.order.clientproxy.common
import com.junbo.common.id.UserId
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.v1.model.User
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.OrderItemRevision
import com.junbo.order.spec.model.enums.OrderItemRevisionType
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
    public static final String ISO8601 = 'yyyy-MM-dd\'T\'HH:mm:ssXXX'

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    def ret = new SimpleDateFormat(ISO8601, Locale.US)
                    ret.timeZone = TimeZone.getTimeZone('UTC');
                    return ret
                }
            }

    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeBuilder)

    static FulfilmentRequest buildFulfilmentRequest(Order order) {
        FulfilmentRequest request = new FulfilmentRequest()
        request.userId = order.user.value
        request.orderId = order.getId().value
        request.trackingUuid = UUID.randomUUID()
        request.shippingMethodId = order.shippingMethod

        request.shippingAddressId = order.shippingAddress?.value
        request.shippingToNameId = order.shippingToName?.value
        request.shippingToPhoneId = order.shippingToPhone?.value
        request.items = []
        order.orderItems?.each { OrderItem item ->
            request.items << buildFulfilmentItem(item)
        }
        return request
    }

    static FulfilmentRequest buildRevokeFulfilmentRequest(Order order) {
        FulfilmentRequest request = new FulfilmentRequest()
        request.userId = order.user.value
        request.orderId = order.getId().value
        request.trackingUuid = UUID.randomUUID()
        request.shippingMethodId = order.shippingMethod

        request.shippingAddressId = order.shippingAddress?.value
        request.shippingToNameId = order.shippingToName?.value
        request.shippingToPhoneId = order.shippingToPhone?.value
        request.items = []

        order.orderItems?.each { OrderItem oi ->

            if (oi.totalAmount != 0) {
                // if there is amount not refund for item, do not revoke it.
                return
            }

            def revokeItems = oi.orderItemRevisions?.findAll { OrderItemRevision oir ->
                oir.revisionType == OrderItemRevisionType.REFUND.name() && !oir.revoked
            }
            if (!CollectionUtils.isEmpty(revokeItems)) {
                def revokeFulfillmentItem = buildFulfilmentItem(oi)
                revokeFulfillmentItem.quantity = 0
                // read from order item revision and accumulate the quantity
                revokeItems.each { OrderItemRevision oir ->
                    revokeFulfillmentItem.quantity += oir.quantity
                }
                if (revokeFulfillmentItem.quantity > oi.quantity) {
                    LOGGER.error('name=Order_Build_Revoke_Fulfillment_Request. Please check. Order :'
                            + order.getId().value + 'Item :'
                            + oi.getId().value + 'quantity: '
                            + revokeFulfillmentItem.quantity.toString() + 'is beyond the original value: '
                            + oi.quantity.toString())
                    revokeFulfillmentItem.quantity = oi.quantity
                }
                request.items << revokeFulfillmentItem
            }

        }

        return request
    }

    private static FulfilmentItem buildFulfilmentItem(OrderItem orderItem) {
        FulfilmentItem item = new FulfilmentItem()
        item.itemReferenceId = orderItem.getId().value
        item.offerId = orderItem.offer.value
        item.timestamp = orderItem.honoredTime?.time
        item.quantity = orderItem.quantity
        return item
    }

    static RatingRequest buildRatingRequest(Order order) {
        RatingRequest request = new RatingRequest()
        List<String> coupons = []
        order.discounts?.each { Discount d ->
            if (d.coupon != null && !d.coupon.isEmpty()) {
                coupons.add(d.coupon)
            }
        }
        request.coupons = ((String[])coupons?.toArray()) as Set
        request.country = order.country
        request.currency = order.currency
        request.userId = order.user?.value
        request.shippingMethodId = order.shippingMethod
        assert(order.honoredTime != null)
        //request.time = DATE_FORMATTER.get().format(order.honoredTime)
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

    static Email buildOrderConfirmationEmail(Order order, User user, String username, List<Offer> offers, EmailTemplate template) {
        Email email = new Email()
        email.userId = (UserId)(user.id)
        email.templateId = template.getId()
        // TODO: update email address as IDENTITY component
        Map<String, String> properties = [:]
        properties.put(ORDER_NUMBER, order.getId().value.toString())
        Date now = new Date()
        properties.put(ORDER_DATE, DATE_FORMATTER.get().format(now))
        properties.put(NAME, username)
        properties.put(SUBTOTAL, order.totalAmount?.toString())
        properties.put(TAX, BigDecimal.ZERO.toString())
        def grandTotal = order.totalAmount
        if (order.totalTax != null) {
            grandTotal += order.totalTax
            properties.put(TAX, order.totalTax.toString())
        }
        properties.put(GRAND_TOTAL, grandTotal.toString())
        offers.eachWithIndex { Offer offer, int index ->
            // TODO update the l10n logic per catalog change
            String offerName = offer.locales[template.locale] != null ? offer.locales[template.locale].name :
                    (offer.locales['DEFAULT'] != null ? offer.locales['DEFAULT'].name : '')
            properties.put(OFFER_NAME + index, offerName)
            order.orderItems.each { OrderItem item ->
                if (item.offer.value == offer.id) {
                    properties.put(QUANTITY + index, item.quantity.toString())
                    properties.put(PRICE + index, (item.quantity * item.unitPrice).toString())
                }
            }
        }

        email.replacements = properties
        return email
    }
}
