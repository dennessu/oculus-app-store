package com.junbo.order.core.impl.common

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.common.id.OrderId
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.model.*
import com.junbo.rating.spec.model.request.OrderRatingItem
import com.junbo.rating.spec.model.request.OrderRatingRequest

/**
 * Created by chriszhu on 2/24/14.
 */
class CoreBuilder {

    static Balance buildBalance(OrderServiceContext context, BalanceType balanceType) {
        if (context == null || context.order == null) {
            return null
        }

        Balance balance = new Balance()
        balance.country = context.order.country
        balance.currency = context.order.currency
        balance.orderId = context.order.id
        balance.piId = context.order.paymentInstruments?.get(0)
        balance.type = balanceType.toString()

        context.order.orderItems.each {
            OrderItem item -> balance.addBalanceItem(buildBalanceItem(item))
        }

        return balance
    }

    static BalanceItem buildBalanceItem(OrderItem item) {
        if (item == null) {
            return null
        }

        BalanceItem balanceItem = new BalanceItem()
        balanceItem.amount = item.totalAmount
        if (item.totalDiscount > BigDecimal.ZERO) {
            DiscountItem discountItem = new DiscountItem()
            discountItem.discountAmount = item.totalDiscount
            balanceItem.addDiscountItem(discountItem)
        }
        return balanceItem
    }

    static void fillRatingInfo(Order order, OrderRatingRequest ratingRequest) {
        order.ratingInfo = buildRatingInfo(ratingRequest)
        for (OrderItem i in order.orderItems) {
            i = CoreBuilder.buildItemRatingInfo(i, ratingRequest)
        }
        // TODO append returned promotions to order
    }

    static RatingInfo buildRatingInfo(OrderRatingRequest ratingRequest) {
        RatingInfo ratingInfo = new RatingInfo()
        ratingInfo.totalAmount = ratingRequest.orderBenefit?.finalAmount
        ratingInfo.totalDiscount = ratingRequest.orderBenefit?.discountAmount
        ratingInfo.totalShippingFee = ratingRequest.shippingFee
        // TODO the shipping discount is not exposed by rating yet
        ratingInfo.totalShippingFeeDiscount = BigDecimal.ZERO
        // TODO the honorUntilTime is not exposed by rating yet
        ratingInfo.honorUntilTime = null
        // TODO support preorder amount

        return ratingInfo
    }

    static OrderItem buildItemRatingInfo(OrderItem item, OrderRatingRequest ratingRequest) {

        OrderRatingItem ratingItem = null
        for (OrderRatingItem i in ratingRequest.lineItems) {
            if (item.offer == i.offerId) {
                ratingItem = i
                break
            }
        }
        if (ratingItem == null) {
            return item
        }
        item.totalAmount = ratingItem.finalAmount
        item.totalDiscount = ratingItem.discountAmount
        return item
    }

    static OrderEvent buildOrderEvent(OrderId orderId, OrderActionType action, EventStatus status) {
        def event = new OrderEvent()
        event.order = orderId
        event.action = action
        event.status = status.name()
        return event
    }
}
