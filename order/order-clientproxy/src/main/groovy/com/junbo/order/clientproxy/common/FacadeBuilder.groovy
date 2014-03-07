package com.junbo.order.clientproxy.common

import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
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
        item.orderItemId = orderItem.id.value
        item.offerId = orderItem.offer
        item.timestamp = Integer.parseInt(orderItem.offerRevision)
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
            ratingItem.offerId = item.offer
            ratingItem.quantity = item.quantity
            ratingItems.add(ratingItem)
        }
        request.lineItems = ((OrderRatingItem[])ratingItems.toArray()) as Set
        return request
    }
}
