package com.junbo.order.core.impl.common
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.PromotionId
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.core.impl.orderaction.context.OrderActionResult
import com.junbo.order.db.entity.enums.DiscountType
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.rating.spec.model.request.OrderRatingItem
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
/**
 * Created by chriszhu on 2/24/14.
 */
@CompileStatic
class CoreBuilder {

    static Balance buildBalance(OrderServiceContext context, BalanceType balanceType) {
        if (context == null || context.order == null) {
            return null
        }

        Balance balance = new Balance()
        balance.country = context.order.country
        balance.currency = context.order.currency
        balance.orderId = context.order.id
        balance.userId = context.order.user
        balance.piId = context.order.paymentInstruments?.get(0)
        balance.type = balanceType.toString()
        balance.trackingUuid = UUID.randomUUID()

        context.order.orderItems.eachWithIndex { OrderItem item, int i ->
            def balanceItem = buildBalanceItem(item)
            if (item.orderItemId == null) {
                balanceItem.orderItemId = new OrderItemId(i)
            } else {
                balanceItem.orderItemId = item.orderItemId
            }
            balance.addBalanceItem(balanceItem)
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
        order.totalAmount = ratingRequest.orderBenefit.finalAmount
        order.totalDiscount = ratingRequest.orderBenefit.discountAmount
        order.totalShippingFee = ratingRequest.shippingBenefit.shippingFee
        // TODO the shipping discount is not exposed by rating yet
        order.totalShippingFeeDiscount = BigDecimal.ZERO
        // TODO the honorUntilTime is not exposed by rating yet
        order.honorUntilTime = null
        // TODO support preorder amount
        for (OrderItem i in order.orderItems) {
            buildItemRatingInfo(i, ratingRequest)
        }
        order.discounts = []
        ratingRequest.couponCodes?.each { String couponCode ->
            def discount = new Discount()
            discount.discountAmount = ratingRequest.orderBenefit.discountAmount
            discount.discountType = DiscountType.ORDER_DISCOUNT
            discount.promotion = new PromotionId(ratingRequest.orderBenefit.promotion)
            order.discounts.add(discount)
            // TODO: need to discuss the coupon logic
            discount.coupon = couponCode
        }

        ratingRequest.lineItems?.each { OrderRatingItem ri ->
            if (!CollectionUtils.isEmpty(ri.promotions)) {
                def d = buildDiscount(ri)
                d.ownerOrderItem = order.orderItems.find { OrderItem oi ->
                    oi.offer.value == ri.offerId
                }
                order.discounts.add(d)
            }
        }
    }

    static Discount buildDiscount(OrderRatingItem ri) {
        def discount = new Discount()
        discount.discountAmount = ri.discountAmount
        discount.discountType = DiscountType.OFFER_DISCOUNT
        // TODO: need to discuss the coupon logic
        if (CollectionUtils.isEmpty(ri.promotions)) {
            discount.promotion = new PromotionId(ri.promotions[0])
        }
        return discount
    }


    static OrderItem buildItemRatingInfo(OrderItem item, OrderRatingRequest ratingRequest) {
        OrderRatingItem ratingItem = ratingRequest.lineItems?.find { OrderRatingItem i ->
            item.offer.value == i.offerId
        }
        if (ratingItem == null) {
            return item
        }
        item.totalAmount = ratingItem.finalAmount
        item.totalDiscount = ratingItem.discountAmount
        // todo get unit price from rating
        item.unitPrice =
                ratingItem.quantity == 0 ? ratingItem.originalAmount : ratingItem.originalAmount / ratingItem.quantity
        item.honorUntilTime = null
        return item
    }

    static void fillTaxInfo(Order order, Balance balance) {
        order.totalTax = balance.taxAmount
        order.isTaxInclusive = balance.taxIncluded
        if (balance.taxIncluded == null) { // todo : remove this, this is a temporary workaround
            order.isTaxInclusive = false
        }

        order.orderItems.eachWithIndex { OrderItem orderItem, int i ->
            def orderItemId = orderItem.orderItemId == null ? new OrderItemId(i) : orderItem.orderItemId
            def balanceItem = balance.balanceItems.find { BalanceItem balanceItem ->
                return balanceItem.orderItemId == orderItemId
            }
            if (balanceItem != null) {
                orderItem.totalTax = balanceItem.taxAmount
                orderItem.isTaxExempted = balanceItem.isTaxExempt
                if (orderItem.isTaxExempted == null) { // todo : remove this, this is a temporary workaround
                    orderItem.isTaxExempted = false
                }
            }
        }
    }

    static OrderEvent buildOrderEvent(OrderId orderId, OrderActionType action,
                                      EventStatus status, String flowType, UUID trackingUuid) {
        def event = new OrderEvent()
        event.order = orderId
        event.action = action
        event.status = status.name()
        event.flowType = flowType
        event.trackingUuid = trackingUuid
        return event
    }

    static ActionResult  buildActionResultForOrderEventAwareAction(OrderActionContext context,
                                                                   EventStatus eventStatus) {
        def orderActionResult = new OrderActionResult()
        orderActionResult.orderActionContext = context
        orderActionResult.returnedEventStatus = eventStatus

        def data = [:]
        data.put(ActionUtils.DATA_ORDER_ACTION_RESULT, (Object)orderActionResult)
        def actionResult = new ActionResult('success', data)
        return actionResult
    }

    static EventStatus buildEventStatusFromBalance(String balanceStatus) {
        switch (balanceStatus) {
            case BalanceStatus.COMPLETED:
                return EventStatus.COMPLETED
            case BalanceStatus.AWAITING_PAYMENT:
                return EventStatus.PENDING
            case BalanceStatus.PENDING_CAPTURE: // Authorize completed
                return EventStatus.COMPLETED
            case BalanceStatus.UNCONFIRMED:
                return EventStatus.PROCESSING
            case BalanceStatus.INIT:
                return EventStatus.PROCESSING
            case BalanceStatus.CANCELLED:
            case BalanceStatus.FAILED:
            case BalanceStatus.ERROR:
                return EventStatus.FAILED
            default:
                return EventStatus.FAILED
        }
    }
}
