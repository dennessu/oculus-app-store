package com.junbo.order.core.common

import com.junbo.common.id.*
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.state.Conversation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.entity.enums.DiscountType
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.ItemType
import com.junbo.order.db.entity.enums.OrderActionType
import com.junbo.order.db.entity.enums.OrderType
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.payment.spec.model.PaymentInstrument
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
        def orderItem = buildOrderItem()
        order.setOrderItems([orderItem])
        order.setType(OrderType.PAY_IN.toString())
        order.setCountry('US')
        order.setCurrency('USD')
        def userId = new UserId()
        userId.setValue(generateLong())
        order.setUser(userId)
        order.setPaymentInstruments([])
        order.paymentInstruments.add(new PaymentInstrumentId(generateLong()))
        order.setShippingAddressId(new ShippingAddressId(generateLong()))
        order.setShippingMethodId(generateLong())
        order.setTentative(true)
        order.setTrackingUuid(generateUUID())
        order.discounts = []
        order.discounts.add(buildDiscount('AAA', orderItem))
        return order
    }

    static Discount buildDiscount(String coupon, OrderItem item) {
        def discount = new Discount()
        discount.coupon = coupon
        //discount.ownerOrderItem = item
        discount.discountAmount = 10.00G
        discount.type = DiscountType.ORDER_DISCOUNT
        return discount
    }

    static OrderItem buildOrderItem() {
        def orderItem = new OrderItem()
        orderItem.setType(ItemType.DIGITAL.toString())
        orderItem.setOffer(new OfferId(generateLong()))
        orderItem.quantity = 1
        orderItem.unitPrice = 10.00G
        return orderItem
    }

    static PaymentInstrument buildCreditCartPI() {
        def pi = new PaymentInstrument()
        pi.type = 'CREDIT_CARD'
        return pi
    }

    static OrderServiceContext buildDefaultContext() {
        def context = new OrderServiceContext(buildOrderRequest())
        return context
    }

    static ActionContext buildActionContext(Order order) {
        def orderActionContext = new OrderActionContext()
        orderActionContext.orderServiceContext = new OrderServiceContext(order)
        def actionContext = new ActionContext(new Conversation(), new HashMap<String, Object>())
        ActionUtils.putOrderActionContext(orderActionContext, actionContext)
        return actionContext
    }

    static OrderEvent buildOrderEvent(OrderId orderId, OrderActionType actionType, EventStatus status) {
        def event = new OrderEvent()
        event.order = orderId
        event.action = actionType == null ? null : actionType.name()
        event.status = status == null ? null : status.name()
        return event
    }

    static FulfilmentRequest buildFulfilmentRequest(Order order) {
        def request = new FulfilmentRequest()
        request.items = []
        request.orderId = order.id.value
        request.trackingGuid = UUID.randomUUID().toString()
        return request
    }

    static FulfilmentItem buildFulfilmentItem(String itemStatus) {
        def item = new FulfilmentItem()
        item.fulfilmentId = generateLong()
        item.orderItemId = generateLong()
        item.status = itemStatus
        return item
    }
}
