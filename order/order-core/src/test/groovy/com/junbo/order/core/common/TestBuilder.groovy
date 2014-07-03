package com.junbo.order.core.common

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.*
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.state.Conversation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.spec.model.enums.DiscountType
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.ItemType
import com.junbo.order.spec.model.enums.OrderActionType
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

    static String generateString() {
        sleep(1)
        return System.currentTimeMillis().toString()
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
        order.setShippingToName(new UserPersonalInfoId(generateLong()))
        order.setShippingToPhone(new UserPersonalInfoId(generateLong()))
        order.setShippingMethod(generateString())
        order.setTentative(true)
        order.discounts = []
        order.discounts.add(buildDiscount('AAA', orderItem))
        order.locale = new LocaleId('en-US')
        order.honoredTime = new Date()
        order.purchaseTime = new Date()
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
        orderItem.setOffer(new OfferId(generateString()))
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
        request.trackingUuid = UUID.randomUUID().toString()
        return request
    }

    static FulfilmentItem buildFulfilmentItem(String itemStatus, OrderItem orderItem) {
        def item = new FulfilmentItem()
        item.fulfilmentId = generateLong()
        item.itemReferenceId = orderItem.getId().value
        item.actions = []
        item.actions  << new FulfilmentAction()
        item.actions[0].status = itemStatus
        return item
    }
}
