package com.junbo.order.core.impl.common
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.common.id.ItemId
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.id.OfferId
import com.junbo.common.id.OfferRevisionId
import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.PromotionId
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.core.impl.orderaction.ActionUtils
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.core.impl.orderaction.context.OrderActionResult
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.DiscountType
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

/**
 * Created by chriszhu on 2/24/14.
 */
@CompileStatic
@TypeChecked
class CoreBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreBuilder)

    public static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    def ret = new SimpleDateFormat('MM/yyyy', Locale.US)
                    ret.timeZone = TimeZone.getTimeZone('UTC')
                    return ret
                }
            }

    public static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER2 =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    def ret = new SimpleDateFormat('yyyy-MM', Locale.US)
                    ret.timeZone = TimeZone.getTimeZone('UTC')
                    return ret
                }
            }

    static Balance buildBalance(Order order, BalanceType balanceType) {
        if (order == null) {
            return null
        }

        Balance balance = buildBalance(order)
        balance.type = balanceType.toString()
        balance.propertySet.put(PropertyKey.LOCALE.name(), order.locale.value?.replace('-', '_'))
        balance.propertySet.put(PropertyKey.IP_ADDRESS.name(), order.ipAddress)
        balance.propertySet.put(PropertyKey.IP_GEO_LOCATION.name(), order.ipGeoAddress)
        order.orderItems.eachWithIndex { OrderItem item, int i ->
            def balanceItem = buildBalanceItem(item)
            if (item.id == null) {
                balanceItem.orderItemId = new OrderItemId(new Long(i))
            } else {
                balanceItem.orderItemId = item.getId()
            }
            balance.addBalanceItem(balanceItem)
        }

        // TODO: confirm whether tax calculation of shipping fee is necessary
        return balance
    }

    static Balance buildPartialChargeBalance(Order order, BalanceType balanceType, Balance taxedBalance) {
        if (order == null) {
            return null
        }

        Balance balance = null
        if (taxedBalance == null) {
            balance = buildBalance(order)
            balance.type = balanceType.toString()
        } else {
            balance = taxedBalance
        }
        balance.skipTaxCalculation = true

        order.orderItems.eachWithIndex { OrderItem item, int i ->
            def balanceItem = buildOrUpdatePartialChargeBalanceItem(item, taxedBalance)
            if (taxedBalance == null) {
                if (item.id == null) {
                    balanceItem.orderItemId = new OrderItemId(new Long(i))
                } else {
                    balanceItem.orderItemId = item.getId()
                }
                balance.addBalanceItem(balanceItem)
            }
        }

        return balance
    }

    static List<Balance> buildRefundBalances(List<Balance> originalBalances, Order diffOrder) {
        assert (originalBalances != null)
        if (CollectionUtils.isEmpty(diffOrder.orderItems)) {
            return []
        }

        List<Balance> returnBalances = []
        originalBalances.each { Balance b ->
            Balance balance = buildRefundBalance(b)
            if (diffOrder.refundPaymentInstrument != null) {
                balance.piId = diffOrder.refundPaymentInstrument
            }
            b.balanceItems.each { BalanceItem item ->
                def matched = diffOrder.orderItems.find { OrderItem orderItem ->
                    orderItem.getId().value == item.orderItemId.value
                }
                if (matched != null) {
                    def balanceItem = buildRefundBalanceItem(item)
                    balanceItem.orderItemId = matched.getId()
                    balanceItem.propertySet.put(PropertyKey.ITEM_QUANTITY.name(), matched.quantity.toString())
                    balanceItem.taxAmount = matched.totalTax
                    balance.addBalanceItem(balanceItem)
                }
                balance.propertySet.put(PropertyKey.INVOICE_DATE.name(),
                        b.propertySet.get(PropertyKey.INVOICE_DATE.name()))
            }
            if (balance.balanceItems?.every { BalanceItem balanceItem ->
                balanceItem.taxAmount != null
            }) {
                // use tax amount directly for fully refund
                balance.skipTaxCalculation = true
            }
            returnBalances << balance
        }

        // handle partial refund
        diffOrder.orderItems.each { OrderItem diffItem ->
            def totalAmount = diffItem.totalAmount
            returnBalances.each { Balance b ->
                BalanceItem balanceItem = b.balanceItems.find { BalanceItem bi ->
                    bi.orderItemId.value == diffItem.getId().value
                }
                assert(balanceItem != null)
                if (totalAmount >= balanceItem.amount) {
                    totalAmount = diffItem.totalAmount - balanceItem.amount
                } else {
                    balanceItem.amount = diffItem.totalAmount
                    totalAmount = 0G
                }
            }
            // validate
            if (totalAmount != 0G) {
                LOGGER.error('name=Refund_Item_Amount_Exceeded')
            }
        }
        return returnBalances
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
        String orderType = CoreUtils.getOrderType(order)
        if (orderType != null) {
            balance.propertySet.put(PropertyKey.ORDER_TYPE.name(), orderType)
        }

        return balance
    }

    static Balance buildRefundBalance(Balance originalBalance) {
        Balance balance = new Balance()
        balance.country = originalBalance.country
        balance.currency = originalBalance.currency
        balance.orderIds = originalBalance.orderIds
        balance.userId = originalBalance.userId
        balance.piId = originalBalance.piId
        balance.trackingUuid = UUID.randomUUID()
        balance.shippingAddressId = originalBalance.shippingAddressId
        balance.providerConfirmUrl = originalBalance.providerConfirmUrl
        balance.successRedirectUrl = originalBalance.successRedirectUrl
        balance.cancelRedirectUrl = originalBalance.cancelRedirectUrl
        balance.originalBalanceId = originalBalance.getId()
        balance.type = BalanceType.REFUND.name()
        balance.setId(null)

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
        if (item.getId() != null) {
            balanceItem.propertySet.put(PropertyKey.ORDER_ITEM_ID.name(), item.getId().value.toString())
        }
        balanceItem.propertySet.put(PropertyKey.ITEM_NAME.name(), item.offerName)
        balanceItem.propertySet.put(PropertyKey.ITEM_DESCRIPTION.name(), item.offerDescription)
        balanceItem.propertySet.put(PropertyKey.ORGANIZATION_ID.name(), item.offerOrganization)
        balanceItem.propertySet.put(PropertyKey.VENDOR_NAME.name(), item.offerOrganizationName)
        if (item.totalDiscount > BigDecimal.ZERO) {
            DiscountItem discountItem = new DiscountItem()
            discountItem.discountAmount = item.totalDiscount
            balanceItem.addDiscountItem(discountItem)
        }
        return balanceItem
    }

    static BalanceItem buildRefundBalanceItem(BalanceItem item) {
        if (item == null) {
            return null
        }
        BalanceItem balanceItem = new BalanceItem()
        balanceItem.amount = item.amount
        DiscountItem discountItem = new DiscountItem()
        discountItem.discountAmount = item.discountAmount
        balanceItem.addDiscountItem(discountItem)
        balanceItem.orderItemId = item.orderItemId
        balanceItem.originalBalanceItemId = item.getId()
        balanceItem.propertySet = item.propertySet
        return balanceItem
    }

    static BalanceItem buildOrUpdatePartialChargeBalanceItem(OrderItem item, Balance taxedBalance) {
        if (item == null) {
            return null
        }

        BalanceItem balanceItem = null
        if (taxedBalance != null) {
            // complete charge
            balanceItem = taxedBalance.balanceItems.find { BalanceItem taxedItem ->
                taxedItem.orderItemId.value == item.getId().value
            }
            balanceItem.amount = item.totalAmount - item.preorderAmount
            taxedBalance.totalAmount -= item.preorderAmount
        } else {
            balanceItem = new BalanceItem()
            balanceItem.amount = item.preorderAmount
        }
        return balanceItem
    }

    static void fillRatingInfo(Order order, RatingRequest ratingRequest) {
        order.totalAmount = ratingRequest.ratingSummary.finalAmount
        order.totalDiscount = ratingRequest.ratingSummary.discountAmount
        order.totalShippingFee = ratingRequest.shippingSummary.totalShippingFee
        // TODO the shipping discount is not exposed by rating yet
        order.totalShippingFeeDiscount = BigDecimal.ZERO
        // TODO the honorUntilTime is not exposed by rating yet
        order.honorUntilTime = null
        for (OrderItem i in order.orderItems) {
            buildItemRatingInfo(i, ratingRequest)
        }
        order.discounts = []
        ratingRequest.coupons?.each { String couponCode ->
            def discount = new Discount()
            discount.discountAmount = ratingRequest.ratingSummary.discountAmount
            discount.discountType = DiscountType.ORDER_DISCOUNT
            discount.promotion = new PromotionId(ratingRequest.ratingSummary.promotion)
            order.discounts.add(discount)
            // TODO: need to discuss the coupon logic
            discount.coupon = couponCode
        }

        ratingRequest.lineItems?.each { RatingItem ri ->
            if (!CollectionUtils.isEmpty(ri.promotions)) {
                def d = buildDiscount(ri)
                d.ownerOrderItem = order.orderItems.find { OrderItem oi ->
                    oi.offer.value == ri.offerId
                }
                order.discounts.add(d)
            }
        }
        order.totalTax = order.totalTax ?: BigDecimal.ZERO
        order.isTaxInclusive = order.isTaxInclusive ?: false
    }

    static Discount buildDiscount(RatingItem ri) {
        def discount = new Discount()
        discount.discountAmount = ri.totalDiscountAmount
        discount.discountType = DiscountType.OFFER_DISCOUNT
        // TODO: need to discuss the coupon logic
        if (CollectionUtils.isEmpty(ri.promotions)) {
            discount.promotion = new PromotionId(ri.promotions[0])
        }
        return discount
    }


    static OrderItem buildItemRatingInfo(OrderItem item, RatingRequest ratingRequest) {
        RatingItem ratingItem = ratingRequest.lineItems?.find { RatingItem i ->
            item.offer.value == i.offerId
        }
        if (ratingItem == null) {
            return item
        }
        item.totalAmount = ratingItem.finalTotalAmount
        if (ratingItem.preOrderPrice != null && ratingItem.preOrderPrice != BigDecimal.ZERO) {
            item.preorderAmount = ratingItem.preOrderPrice
        }
        item.developerRevenue = ratingItem.developerRevenue
        item.totalDiscount = ratingItem.totalDiscountAmount
        item.unitPrice = ratingItem.originalUnitPrice
        item.honorUntilTime = null
        item.totalTax = item.totalTax ?: BigDecimal.ZERO
        return item
    }

    static void fillTaxInfo(Order order, Balance balance) {
        order.totalTax = balance.taxAmount
        order.isTaxInclusive = balance.taxIncluded
        if (balance.taxIncluded == null) { // todo : remove this, this is a temporary workaround
            order.isTaxInclusive = false
        }

        order.orderItems.eachWithIndex { OrderItem orderItem, int i ->
            def orderItemId = orderItem.id == null ? new OrderItemId(new Long(i)) : orderItem.id
            def balanceItem = balance.balanceItems.find { BalanceItem balanceItem ->
                return balanceItem.orderItemId == orderItemId
            }
            if (balanceItem != null) {
                orderItem.totalTax = balanceItem.taxAmount
                fillTaxItem(orderItem, balanceItem)
            }
        }
    }

    static void mergeTaxInfo(List<Balance> balances, OrderItem orderItem) {
        def init = true
        balances?.eachWithIndex { Balance bl, int i ->
            if (!CoreUtils.isValidBalance(bl)) {
                return
            }
            def direction = 1
            if (bl.type == BalanceType.REFUND.name() || bl.type == BalanceType.CREDIT.name()) {
                direction = -1
            }
            def balanceItem = bl.balanceItems.find { BalanceItem balanceItem ->
                return balanceItem.orderItemId.value == orderItem.getId().value
            }
            if (balanceItem != null) {
                if(init) {
                    // init the tax item
                    orderItem.taxes = []
                    fillTaxItem(orderItem, balanceItem, direction)
                    init = false
                } else {
                    orderItem.taxes?.each() { OrderTaxItem oti ->
                        def taxItem = balanceItem.taxItems?.find() { TaxItem bti ->
                            // TODO: check taxType
                            bti.taxAuthority == oti.taxType
                        }
                        if (taxItem != null) {
                            oti.taxAmount += taxItem.taxAmount * direction
                        }
                    }
                }
            }
        }

    }

    static void fillTaxItem(OrderItem orderItem, BalanceItem balanceItem) {
        fillTaxItem(orderItem, balanceItem, 1)
    }

    static void fillTaxItem(OrderItem orderItem, BalanceItem balanceItem, int direction) {
        def taxes = []
        balanceItem.taxItems.each { TaxItem taxItem ->
            def orderTaxItem = new OrderTaxItem()
            orderTaxItem.taxAmount = taxItem.taxAmount * direction
            orderTaxItem.taxRate = taxItem.taxRate
            orderTaxItem.taxType = taxItem.taxAuthority
            orderTaxItem.isTaxExempted = taxItem.isTaxExempt ?: false

            taxes << orderTaxItem
        }
        orderItem.taxes = taxes
    }

    static OrderEvent buildOrderEvent(OrderId orderId, OrderActionType action,
                                      EventStatus status, String flowName, UUID trackingUuid) {
        def event = new OrderEvent()
        event.order = orderId
        event.action = action
        event.status = status.name()
        event.flowName = flowName
        event.trackingUuid = trackingUuid
        return event
    }

    static ActionResult buildActionResultForOrderEventAwareAction(OrderActionContext context,
                                                                  String eventStatus) {
        return buildActionResultForOrderEventAwareAction(context, EventStatus.valueOf(eventStatus))
    }

    static ActionResult buildActionResultForOrderEventAwareAction(OrderActionContext context,
                                                                  EventStatus eventStatus) {
        return buildActionResultForOrderEventAwareAction(context, eventStatus, 'success')
    }

    static ActionResult buildActionResultForOrderEventAwareAction(OrderActionContext context,
                                                                  EventStatus eventStatus, String actionResultStr) {
        def orderActionResult = new OrderActionResult()
        orderActionResult.orderActionContext = context
        orderActionResult.returnedEventStatus = eventStatus

        def data = [:]
        data.put(ActionUtils.DATA_ORDER_ACTION_RESULT, (Object) orderActionResult)
        def actionResult = new ActionResult(actionResultStr, data)
        return actionResult
    }


    static Date buildDate(String date) {
        if (date.contains('/')) {
            return DATE_FORMATTER.get().parse(date)
        }
        else {
            return DATE_FORMATTER2.get().parse(date)
        }
    }

    static OfferSnapshot buildOfferSnapshot(Offer offer) {
        OfferSnapshot offerSnapshot = new OfferSnapshot()
        offerSnapshot.offer = new OfferId(offer.id)
        offerSnapshot.offerRevision = new OfferRevisionId(offer.revisionId)
        def itemSnapshots = []
        offer.itemIds?.keySet().each { String key ->
            itemSnapshots.add(new ItemSnapshot(
                    item: new ItemId(key),
                    itemRevision: new ItemRevisionId(offer.itemIds.get(key))
            ))
        }
        offerSnapshot.itemSnapshots = itemSnapshots
        return offerSnapshot
    }
}
