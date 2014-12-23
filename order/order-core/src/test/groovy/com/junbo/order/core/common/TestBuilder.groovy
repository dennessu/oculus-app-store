package com.junbo.order.core.common

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TaxAuthority
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.billing.spec.model.TaxItem
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
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.DiscountType
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.ItemType
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.rating.spec.model.priceRating.RatingSummary
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import java.text.SimpleDateFormat

/**
 * Created by chriszhu on 2/14/14.
 */
@CompileStatic
@TypeChecked
class TestBuilder {

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
        order.totalAmount = 99.99G
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
        orderItem.unitPrice = 99.99G
        orderItem.honoredTime = new Date()
        return orderItem
    }

    static PaymentInstrument buildCreditCartPI() {
        def pi = new PaymentInstrument()
        pi.type = PIType.CREDITCARD.id
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

    static Balance buildBalance(Order order) {
        Balance balance = new Balance()
        balance.country = order.country.value
        balance.currency = order.currency.value
        balance.orderIds = [order.getId()]
        balance.userId = order.user
        balance.piId = order.payments?.get(0)?.paymentInstrument
        balance.trackingUuid = UUID.randomUUID()
        balance.shippingAddressId = order.shippingAddress
        balance.providerConfirmUrl = order.payments?.get(0)?.providerConfirmUrl
        balance.successRedirectUrl = order.payments?.get(0)?.successRedirectUrl
        balance.cancelRedirectUrl = order.payments?.get(0)?.cancelRedirectUrl
        if (order.paymentDescription != null) {
            balance.propertySet.put(PropertyKey.BALANCE_DESCRIPTION.name(), order.paymentDescription)
        }
        balance.totalAmount = order.totalAmount
        return balance
    }

    static Balance buildBalanceWithItems(Order order, BalanceType balanceType) {
        if (order == null) {
            return null
        }
        Balance balance = buildBalance(order)
        balance.type = balanceType.toString()
        balance.propertySet.put(PropertyKey.LOCALE.name(), order.locale.value?.replace('-', '_'))
        order.orderItems.eachWithIndex { OrderItem item, int i ->
            def balanceItem = buildBalanceItem(item)
            if (item.id == null) {
                balanceItem.orderItemId = new OrderItemId(new Long(i))
            } else {
                balanceItem.orderItemId = item.getId()
            }
            balance.addBalanceItem(balanceItem)
        }
        return balance
    }

    static Balance buildTaxedBalance(Balance balance) {
        balance.status = BalanceStatus.AWAITING_PAYMENT.name()
        balance.taxAmount = 10.00G
        balance.taxIncluded = false
        balance.taxStatus = TaxStatus.TAXED.name()
        balance.totalAmount = balance.totalAmount + balance.taxAmount
        balance.balanceItems.each { BalanceItem item ->
            item.taxAmount = 10.00G
            item.addTaxItem(new TaxItem(
                    taxAmount: 4.00G,
                    taxAuthority: TaxAuthority.STATE.name(),
                    taxRate: 0.04G,
                    isTaxExempt: false,
            ))
            item.addTaxItem(new TaxItem(
                    taxAmount: 6.00G,
                    taxAuthority: TaxAuthority.CITY.name(),
                    taxRate: 0.06G,
                    isTaxExempt: false,
            ))
        }
        return balance
    }

    static BalanceItem buildBalanceItem(OrderItem item) {
        if (item == null) {
            return null
        }
        BalanceItem balanceItem = new BalanceItem()
        balanceItem.amount = item.totalAmount
        balanceItem.orderItemId = item.getId()
        balanceItem.propertySet.put(PropertyKey.ITEM_TYPE.name(), item.type)
        balanceItem.propertySet.put(PropertyKey.ITEM_QUANTITY.name(), item.quantity.toString())
        balanceItem.propertySet.put(PropertyKey.OFFER_ID.name(), item.offer.value)
        if (item.totalDiscount > BigDecimal.ZERO) {
            DiscountItem discountItem = new DiscountItem()
            discountItem.discountAmount = item.totalDiscount
            balanceItem.addDiscountItem(discountItem)
        }
        return balanceItem
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
        request.time = DATE_FORMATTER.get().format(order.honoredTime)
        request.includeCrossOfferPromos = true
        List<RatingItem> ratingItems = []
        order.orderItems?.each { OrderItem item ->
            RatingItem ratingItem = new RatingItem()
            ratingItem.offerId = item.offer.value
            ratingItem.quantity = item.quantity
            ratingItem.developerRevenue = 44.99G
            ratingItem.finalTotalAmount = 49.99G
            ratingItem.originalTotalPrice = 49.99G
            ratingItem.originalUnitPrice = 49.99G
            ratingItem.totalDiscountAmount = 0.00G
            ratingItems.add(ratingItem)
        }
        request.lineItems = ((RatingItem[])ratingItems.toArray()) as Set
        request.ratingSummary = new RatingSummary(
                finalAmount: 99.99G,
                discountAmount: 0.00G,
        )
        return request
    }
}
