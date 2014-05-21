package com.junbo.order.core.common

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.*
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
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by chriszhu on 2/14/14.
 */
@CompileStatic
@TypeChecked
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
        order.setCountry(new CountryId('US'))
        order.setCurrency(new CurrencyId('USD'))
        def userId = new UserId()
        userId.setValue(generateLong())
        order.setUser(userId)
        order.setPayments([])
        order.payments.add(new PaymentInfo(paymentInstrument: new PaymentInstrumentId(generateLong())))
        order.setShippingAddress(new UserPersonalInfoId(generateLong()))
        order.setShippingMethod(generateLong())
        order.setTentative(true)
        order.discounts = []
        order.discounts.add(buildDiscount('AAA', orderItem))
        order.locale = new LocaleId('en_US')
        order.honoredTime = new Date()
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
        orderItem.setId(new OrderItemId(generateLong()))
        orderItem.setType(ItemType.DIGITAL.toString())
        orderItem.setOffer(new OfferId(generateLong()))
        orderItem.quantity = 1
        orderItem.unitPrice = 10.00G
        orderItem.honoredTime = new Date()
        return orderItem
    }

    static PaymentInstrument buildCreditCartPI() {
        def pi = new PaymentInstrument()
        pi.type = generateLong()
        return pi
    }

    static OrderServiceContext buildDefaultContext() {
        def context = new OrderServiceContext(buildOrderRequest())
        return context
    }

    static ActionContext buildActionContext(Order order) {
        def orderActionContext = new OrderActionContext()
        orderActionContext.orderServiceContext = new OrderServiceContext(order)
        orderActionContext.trackingUuid = UUID.randomUUID()
        def actionContext = new ActionContext(new Conversation(), new HashMap<String, Object>())
        ActionUtils.putOrderActionContext(orderActionContext, actionContext)
        ActionUtils.putFlowName('testFlowName', actionContext)
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
        request.orderId = order.getId().value
        request.trackingGuid = UUID.randomUUID().toString()
        return request
    }

    static FulfilmentItem buildFulfilmentItem(String itemStatus, OrderItem orderItem) {
        def item = new FulfilmentItem()
        item.fulfilmentId = generateLong()
        item.orderItemId = orderItem.getId().value
        item.status = itemStatus
        return item
    }
}
