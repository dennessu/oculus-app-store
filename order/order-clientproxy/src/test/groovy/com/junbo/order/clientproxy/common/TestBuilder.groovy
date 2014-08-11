package com.junbo.order.clientproxy.common

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.*
import com.junbo.identity.spec.v1.model.User
import com.junbo.order.spec.model.Discount
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by chriszhu on 2/19/14.
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


    static Balance buildBalance() {
        def balance = new Balance()
        def orderId = new OrderId()
        orderId.value = generateLong()
        balance.orderIds = [orderId]
        balance.setCountry('US')
        balance.setCurrency('USD')
        def piId = new PaymentInstrumentId()
        piId.value = generateLong()
        balance.setPiId(piId)
        balance.setType(BalanceType.DEBIT.toString())
        balance.setTotalAmount(BigDecimal.valueOf(99.99D))
        return balance
    }

    static User buildUser() {
        def user = new User()
        def userId = new UserId()
        userId.value = generateLong()
        user.setId(userId)
        user.setUsername(new UserPersonalInfoId(0L))
        return user
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
        order.getPayments().add(new PaymentInfo(paymentInstrument: new PaymentInstrumentId(generateLong())))
        order.setShippingAddress(new UserPersonalInfoId(generateLong()))
        order.setShippingToName(new UserPersonalInfoId(generateLong()))
        order.setShippingToPhone(new UserPersonalInfoId(generateLong()))
        order.setShippingMethod(generateString())
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
        discount.type = 'ORDER_DISCOUNT'
        return discount
    }

    static OrderItem buildOrderItem() {
        def orderItem = new OrderItem()
        orderItem.setType('DIGITAL')
        orderItem.setOffer(new OfferId(generateString()))
        orderItem.quantity = 1
        orderItem.unitPrice = 10.00G
        return orderItem
    }
}
