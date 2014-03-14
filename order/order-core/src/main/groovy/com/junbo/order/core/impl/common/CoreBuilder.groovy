package com.junbo.order.core.impl.common
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.common.id.OrderId
import com.junbo.common.id.PromotionId
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.core.impl.orderaction.context.OrderActionResult
import com.junbo.order.db.entity.enums.DiscountType
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.entity.enums.OrderStatus
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
        def discount = new Discount()
        discount.discountAmount = ratingRequest.orderBenefit.discountAmount
        discount.discountType = DiscountType.ORDER_DISCOUNT
        discount.promotion = new PromotionId(ratingRequest.orderBenefit.promotion)
        order.discounts.add(discount)
        // TODO: need to discuss the coupon logic
        discount.coupon = ratingRequest.couponCodes[0]
        ratingRequest.lineItems?.each { OrderRatingItem ri ->
            def d = buildDiscount(ri)
            d.ownerOrderItem = order.orderItems.find { OrderItem oi ->
                oi.offer.value == ri.offerId
            }
            order.discounts.add(d)
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

        OrderRatingItem ratingItem = null
        ratingItem = ratingRequest.lineItems?.find { OrderRatingItem i ->
            item.offer.value == i.offerId
        }
        if (ratingItem == null) {
            return item
        }
        item.totalAmount = ratingItem.finalAmount
        item.totalDiscount = ratingItem.discountAmount
        item.honorUntilTime = null
        return item
    }

    static void fillTaxInfo(Order order, Balance balance) {
        order.totalTax = balance.taxAmount
        order.isTaxInclusive = balance.taxIncluded

        for (BalanceItem bi in balance.balanceItems) {
            def orderItem = order.orderItems.find { OrderItem oi ->
                oi.orderItemId == bi.orderItemId

            }
            if (orderItem != null) {
                orderItem?.totalTax = bi.taxAmount
                orderItem?.isTaxExempted = bi.isTaxExempt
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
//    public enum OrderStatus implements Identifiable<Short> {
//        OPEN(0),
//        PENDING_CHARGE(1),
//        PENDING_FULFILL(2),
//        CHARGED(3),
//        FULFILLED(4),
//        COMPLETED(5),
//        FAILED(6),
//        CANCELED(7),
//        REFUNDED(8),
//        ERROR(-1);
    static OrderStatus buildOrderStatus(List<OrderEvent> orderEvents) {
        orderEvents.each {

        }
        return OrderStatus.OPEN
    }
}
