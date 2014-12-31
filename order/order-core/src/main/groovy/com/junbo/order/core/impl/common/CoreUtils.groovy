package com.junbo.order.core.impl.common
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.common.id.EntitlementId
import com.junbo.common.id.OfferId
import com.junbo.fulfilment.spec.constant.FulfilmentActionType
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.*
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by chriszhu on 3/19/14.
 */
@CompileStatic
@TypeChecked
class CoreUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreUtils)

    static Boolean isFreeOrder(Order order) {
        return order.totalAmount == BigDecimal.ZERO
    }

    static Boolean hasPhysicalOffer(Order order) {
        if (CollectionUtils.isEmpty(order.orderItems)) {
            return false
        }
        if (order.orderItems.any { OrderItem oi ->
            oi.type == ItemType.PHYSICAL_GOODS.name()
        }) {
            return true
        }
        return false
    }

    static Boolean hasStoredValueOffer(Order order) {
        if (CollectionUtils.isEmpty(order.orderItems)) {
            return false
        }
        if (order.orderItems.any { OrderItem oi ->
            oi.type == ItemType.GIFT_CARD.name()
        }) {
            return true
        }
        return false
    }

    static Boolean isChargeCompleted(List<Balance> balances) {

        if (CollectionUtils.isEmpty(balances)) {
            return false
        }
        if (balances.any { Balance balance ->
            (balance.status != BalanceStatus.AWAITING_PAYMENT.name() &&
                    balance.status != BalanceStatus.COMPLETED.name()) ||
                    balance.type != BalanceType.DEBIT.name()
        }) {
            return false
        }
        return true
    }

    static Boolean isChargeFailed(List<Balance> balances) {
        if (CollectionUtils.isEmpty(balances)) {
            return false
        }
        if (balances.any { Balance balance ->
            balance.status == BalanceStatus.FAILED.name() && balance.type == BalanceType.DEBIT.name()
        }) {
            return true
        }
        return false
    }

    static Boolean isRateExpired(Order order) {
        if (order.honorUntilTime == null) {
            return true
        }
        if (order.honorUntilTime > new Date()) {
            return true
        }
        return false
    }

    static Order diffRefundOrder(Order existingOrder, Order request, Integer numberAfterDecimal) {
        Order diffOrder = new Order()
        diffOrder.setId(existingOrder.getId())
        diffOrder.orderItems = []
        diffOrder.totalAmount = 0G
        diffOrder.refundPaymentInstrument = request.refundPaymentInstrument

        Boolean changed = false
        existingOrder.orderItems.each { OrderItem i ->
            OrderItem diffItem = new OrderItem()
            diffItem.offer = i.offer
            diffItem.setId(i.getId())
            OrderItem requestItem = request.orderItems?.find { OrderItem ii ->
                i.offer == ii.offer
            }
            if (requestItem == null) {
                diffItem.quantity = i.quantity
                diffItem.totalAmount = i.totalAmount
            } else {
                if (i.quantity > requestItem.quantity) {
                    diffItem.quantity = i.quantity - requestItem.quantity
                    diffItem.totalAmount = (diffItem.quantity * i.totalAmount / i.quantity).setScale(
                            numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
                    requestItem.totalAmount = requestItem.totalAmount - diffItem.totalAmount
                    if (requestItem.quantity == i.quantity) {
                        diffItem.totalTax = i.totalTax
                    }
                } else if (i.quantity == requestItem.quantity && i.totalAmount > requestItem.totalAmount) {
                    diffItem.quantity = 0
                    diffItem.totalAmount = i.totalAmount - requestItem.totalAmount
                    if (requestItem.totalAmount == 0) {
                        diffItem.totalTax = i.totalTax
                    }
                } else {
                    // no change
                    return
                }
            }
            diffOrder.totalAmount += diffItem.totalAmount
            diffOrder.orderItems << diffItem
            changed = true
        }
        if (!changed) {
            throw AppErrors.INSTANCE.orderNoItemRefund().exception()
        }
        return diffOrder
    }

    static Order calcRefundedOrder(Order existingOrder, Balance balance, Order diffOrder) {
        assert (existingOrder != null)
        assert (balance != null)
        assert (diffOrder != null)
        assert (balance.totalAmount != null)
        assert (balance.taxAmount != null)
        assert (balance.discountAmount != null)
        def returnVal = existingOrder
        returnVal.totalAmount -= balance.totalAmount
        if (!balance.taxIncluded) {
            returnVal.totalAmount += balance.taxAmount
        }
        returnVal.totalTax -= balance.taxAmount
        returnVal.totalDiscount -= balance.discountAmount
        assert (returnVal.totalTax >= 0G)
        assert (returnVal.totalAmount >= 0G)
        assert (returnVal.totalDiscount >= 0G)

        returnVal.orderItems?.each() { OrderItem oi ->
            def balanceItem = balance.balanceItems?.find() { BalanceItem bi ->
                bi.orderItemId.value == oi.getId().value
            }
            if (balanceItem != null) {
                assert (balanceItem.amount != null)
                assert (balanceItem.taxAmount != null)
                assert (balanceItem.discountAmount != null)
                oi.totalAmount -= balanceItem.amount
                oi.totalTax -= balanceItem.taxAmount
                oi.totalDiscount -= balanceItem.discountAmount
                if (balance.skipTaxCalculation) {
                    // for full refund, no tax item will be returned
                    oi.taxes = []
                } else {
                    oi.taxes?.each() { OrderTaxItem oti ->
                        def taxItem = balanceItem.taxItems?.find() { TaxItem bti ->
                            // TODO: check taxType
                            bti.taxAuthority == oti.taxType
                        }
                        if (taxItem != null) {
                            oti.taxAmount -= taxItem.taxAmount
                        }
                    }
                }
            }
            def diffItem = diffOrder.orderItems?.find() { OrderItem doi ->
                doi.getId().value == oi.getId().value
            }
            if (diffItem != null) {
                oi.quantity -= diffItem.quantity
            }
            assert (oi.totalAmount >= 0G)
            assert (oi.totalTax >= 0G)
            assert (oi.totalDiscount >= 0G)
            assert (oi.quantity >= 0G)
        }
        return returnVal
    }

    static Boolean isRefundable(Order order) {

        if (order.tentative) {
            return false
        }
        return isChargeCompleted(order) && !isFreeOrder(order)
    }

    static Boolean isCancellable(Order order) {

        if (order.tentative) {
            return true
        }
        return !isChargeCompleted(order)
    }

    static BillingHistory getLatestBillingHistory(Order order) {
        if (CollectionUtils.isEmpty(order.billingHistories)) {
            return null
        }

        order.billingHistories.sort { BillingHistory bh ->
            bh.createdTime
        }.reverse(true)

        return order.billingHistories[0]
    }

    static Boolean isValidBalance(Balance balance) {
        if (balance.status == BalanceStatus.CANCELLED.name() ||
                balance.status == BalanceStatus.ERROR.name() ||
                balance.status == BalanceStatus.FAILED.name() ||
                balance.status == BalanceStatus.INIT.name() ||
                balance.status == BalanceStatus.PENDING_CAPTURE.name()) {
            return false
        }
        return true
    }

    static Boolean checkDepositOrderRefundable(Order order, List<Balance> balances) {

        BillingHistory bh = getLatestBillingHistory(order)
        if (bh.billingEvent != BillingAction.DEPOSIT.name()) {
            return false
        }
        // check there's only one deposit balance and the balance id is same with the billinghistory
        List<Balance> validBalances = balances.findAll { Balance ba ->
            ba.status == isValidBalance(ba)
        }.toList()

        if (validBalances != null && validBalances.size() == 1) {
            def depositBalance = validBalances[0]
            if (depositBalance.type == BalanceType.DEBIT.name() && depositBalance.id == bh.balanceId) {
                return true
            }
        }
        return false
    }

    static Boolean compareOrderRating(Order oldOrder, Order newOrder) {
        return oldOrder.totalDiscount == newOrder.totalDiscount &&
                oldOrder.totalAmount == newOrder.totalAmount &&
                oldOrder.totalTax == newOrder.totalTax &&
                oldOrder.totalShippingFee == newOrder.totalShippingFee &&
                oldOrder.totalShippingFeeDiscount == newOrder.totalShippingFeeDiscount &&
                oldOrder.isTaxInclusive == newOrder.isTaxInclusive
    }

    static Order copyOrderRating(Order order) {
        def newOrder = new Order()
        newOrder.totalAmount = order.totalAmount
        newOrder.totalTax = order.totalTax
        newOrder.totalShippingFee = order.totalShippingFee
        newOrder.totalShippingFeeDiscount = order.totalShippingFeeDiscount
        newOrder.isTaxInclusive = order.isTaxInclusive
        newOrder.totalDiscount = order.totalDiscount
        return newOrder
    }

    static Boolean isPreorder(Offer offer, String country) {
        if (offer.countryReleaseDates == null || offer.countryReleaseDates.get(country) == null) {
            return false
        }
        def releaseDate = offer.countryReleaseDates.get(country)
        def now = new Date()
        return releaseDate != null && releaseDate.after(now)
    }

    static Boolean isPreorder(Order order) {
        if (CollectionUtils.isEmpty(order.orderItems)) {
            return false
        }
        if (order.orderItems.any { OrderItem oi ->
            oi.isPreorder
        }) {
            return true
        }
        return false
    }

    static Boolean isPendingOnEvent(Order order, OrderEvent event) {
        switch (event.action) {
            case OrderActionType.CHARGE.name():
                return order.status == OrderStatus.PENDING.name()
            case OrderActionType.FULFILL.name():
                return order.status == OrderStatus.PENDING.name() || order.status == OrderStatus.PREORDERED.name()
            default:
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
        }
    }

    static void readHeader(Order order, ApiContext context) {
        if (context == null) {
            return
        }

        order.ipAddress = context.userIp
        order.ipGeoAddress = context.location

    }

    static Boolean isPendingOnFulfillment(Order order, OrderEvent event) {
        if (CoreUtils.isPreorder(order)) {
            return order.status == OrderStatus.PREORDERED.name()
        }
        if (!CoreUtils.isChargeCompleted(order)) {
            LOGGER.info('name=Order_Not_Charged')
            return false
        }
        if (hasPhysicalOffer(order)) {
            switch (event.action) {
                case OrderActionType.DELIVER.name():
                    return order.status == OrderStatus.SHIPPED.name()
                case OrderActionType.FULFILL.name():
                    return order.status == OrderStatus.PENDING.name() ||
                            order.status == OrderStatus.PREORDERED.name()
                case OrderActionType.RETURN.name():
                    return order.status == OrderStatus.DELIVERED.name()
                default:
                    LOGGER.info('name=Order_Event_Not_Supported_Physical')
                    return false
            }
        } else {
            switch (event.action) {
                case OrderActionType.FULFILL.name():
                    return order.status == OrderStatus.PENDING.name()
                default:
                    LOGGER.info('name=Order_Event_Not_Supported_Physical')
                    return false
            }
        }
    }

    static Boolean isRefundedOrCanceled(Order order) {
        return isRefunded(order) || isCanceled(order)
    }

    static Boolean isRefunded(Order order) {
        if (CollectionUtils.isEmpty(order.billingHistories)) {
            return false
        }
        return order.billingHistories.any { BillingHistory bh ->
            (bh.billingEvent == BillingAction.REQUEST_REFUND.name() && bh.success) ||
                    (bh.billingEvent == BillingAction.REFUND.name() && bh.success)
        }
    }

    static Boolean isCanceled(Order order) {
        if (CollectionUtils.isEmpty(order.billingHistories)) {
            return false
        }
        return order.billingHistories.any { BillingHistory bh ->
            (bh.billingEvent == BillingAction.CANCEL.name() && bh.success) ||
                    (bh.billingEvent == BillingAction.REQUEST_CANCEL.name() && bh.success)
        }
    }

    static Boolean isChargeCompleted(Order order) {
        if (isFreeOrder(order)) {
            return true
        }

        if (CollectionUtils.isEmpty(order.billingHistories)) {
            return false
        }
        if (isCanceled(order)) {
            return false
        }
        return order.billingHistories.any { BillingHistory bh ->
            (bh.billingEvent == BillingAction.CAPTURE.name() && bh.success) ||
                    (bh.billingEvent == BillingAction.CHARGE.name() && bh.success)
        }
    }

    static Boolean isFulfillCompleted(Order order) {

        def fulfillCompleted = true
        order.orderItems.each { OrderItem oi ->
            if (CollectionUtils.isEmpty(oi.fulfillmentHistories)) {
                fulfillCompleted = false
                return
            }
            if (!oi.fulfillmentHistories.any() { FulfillmentHistory fh ->
                fh.fulfillmentEvent == FulfillmentEventType.FULFILL.name() && fh.success
            }) {
                fulfillCompleted = false
            }
        }
        return fulfillCompleted
    }

    static Boolean isShipCompleted(Order order) {

        def fulfillCompleted = true
        order.orderItems.each { OrderItem oi ->
            if (CollectionUtils.isEmpty(oi.fulfillmentHistories)) {
                fulfillCompleted = false
                return
            }
            if (!oi.fulfillmentHistories.any() { FulfillmentHistory fh ->
                fh.fulfillmentEvent == FulfillmentEventType.SHIP.name() && fh.success
            }) {
                fulfillCompleted = false
            }
        }
        return fulfillCompleted
    }

    static Boolean isPendingOnFulfillment(Order order) {

        if (isFulfillCompleted(order)) {
            return false
        }

        def fulfillPending = false
        order.orderItems.each { OrderItem oi ->
            if (CollectionUtils.isEmpty(oi.fulfillmentHistories)) {
                fulfillPending = false
                return
            }

            if (oi.fulfillmentHistories.any() { FulfillmentHistory fh ->
                fh.fulfillmentEvent == FulfillmentEventType.REQUEST_FULFILL.name() && fh.success
            }) {
                fulfillPending = true
            }
        }
        return fulfillPending
    }

    static Boolean isPreordered(Order order) {

        if (isFulfillCompleted(order)) {
            return false
        }

        def preordered = false
        order.orderItems.each { OrderItem oi ->
            if (CollectionUtils.isEmpty(oi.fulfillmentHistories)) {
                preordered = false
                return
            }

            if (oi.fulfillmentHistories.any() { FulfillmentHistory fh ->
                fh.fulfillmentEvent == FulfillmentEventType.PREORDER.name() && fh.success
            }) {
                preordered = true
            }
        }
        return preordered
    }

    static Boolean isPendingOnCapture(Order order) {
        if (hasPhysicalOffer(order)) {
            if (CollectionUtils.isEmpty(order.billingHistories)) {
                return false
            }
            if (isRefundedOrCanceled(order)) {
                return false
            }
            def authorized = order.billingHistories.any { BillingHistory bh ->
                bh.billingEvent == BillingAction.AUTHORIZE.name() && bh.success
            }
            def captured = order.billingHistories.any { BillingHistory bh ->
                bh.billingEvent == BillingAction.CAPTURE.name() && bh.success
            }
            return authorized && !captured
        }
        LOGGER.info('name=Order_No_Physical')
        return false
    }

    static Boolean isPendingOnChargeConfirmation(Order order) {

        if (CollectionUtils.isEmpty(order.billingHistories)) {
            return false
        }
        if (isRefundedOrCanceled(order)) {
            return false
        }
        def requested = order.billingHistories.any { BillingHistory bh ->
            bh.billingEvent == BillingAction.REQUEST_CHARGE.name() && bh.success
        }
        def charged = order.billingHistories.any { BillingHistory bh ->
            bh.billingEvent == BillingAction.CHARGE.name() && bh.success
        }
        return requested && !charged
    }

    static Order refreshBillingHistories(Order order, List<Balance> balances) {
        if (CollectionUtils.isEmpty(balances)) {
            return order
        }
        if (!CollectionUtils.isEmpty(order.billingHistories)) {
            // fill balance info
            order.billingHistories.each { BillingHistory bh ->
                def balance = balances.find() { Balance ba ->
                    ba.getId().value == Long.parseLong(bh.balanceId)
                }
                assert (balance != null)
                bh.payments = []
                def payment = new BillingPaymentInfo()
                payment.paymentAmount = balance.totalAmount
                payment.paymentInstrument = balance.piId
                bh.payments << payment
                if (balance.type == BalanceType.REFUND.name()) {
                    bh.refundedOrderItems = []
                    balance.balanceItems?.each { BalanceItem bi ->
                        def roi = new RefundOrderItem()
                        roi.orderItemId = bi.orderItemId.value
                        roi.refundedAmount = 0G - bi.amount
                        roi.refundedTax = 0G - bi.taxAmount
                        // fill quantity and offer id
                        roi.offer = new OfferId(bi.propertySet.get(PropertyKey.OFFER_ID.name()))
                        roi.quantity = Integer.parseInt(bi.propertySet.get(PropertyKey.ITEM_QUANTITY.name()))
                        bh.refundedOrderItems << roi
                    }
                    payment.paymentAmount = 0G - balance.totalAmount
                }
            }
        }
        return order
    }

    static Boolean isBalanceSettled(Balance balance) {
        return BalanceStatus.COMPLETED.name() == balance.status ||
                BalanceStatus.AWAITING_PAYMENT.name() == balance.status
    }

    static Order refreshFulfillmentHistories(Order order, FulfilmentRequest fr) {
        if (fr == null) {
            return order
        }
        fr.items?.collect() { FulfilmentItem fi ->
            def fulfillOrderItem = order.orderItems?.find() { OrderItem oi ->
                oi.getId().value == fi.itemReferenceId
            }
            if (fulfillOrderItem != null) {
                fulfillOrderItem.fulfillmentHistories.collect() { FulfillmentHistory fh ->
                    fh.coupons = []
                    fh.walletAmount = 0G
                    fh.shippingDetails = []
                    fh.entitlements = []
                    // do not update the status for request_ actions
                    if (!CollectionUtils.isEmpty(fi.actions)) {
                        fh.success = !fi.actions?.any { FulfilmentAction fa ->
                            fa.status == FulfilmentStatus.FAILED || fa.status == FulfilmentStatus.UNKNOWN
                        }
                    }
                    // TODO: parse shippingDetails and coupons
                    def entitlementAction = fi.actions?.find() { FulfilmentAction fa ->
                        fa.type == FulfilmentActionType.GRANT_ENTITLEMENT &&
                                fa.status != FulfilmentStatus.FAILED && fa.status != FulfilmentStatus.UNKNOWN
                    }
                    if (entitlementAction != null) {
                        entitlementAction.result?.entitlementIds?.each { String eid ->
                            fh.entitlements << new EntitlementId(eid)
                        }
                    }
                    def walletAction = fi.actions?.find() { FulfilmentAction fa ->
                        fa.type == FulfilmentActionType.CREDIT_WALLET &&
                                fa.status != FulfilmentStatus.FAILED && fa.status != FulfilmentStatus.UNKNOWN
                    }
                    if (walletAction != null) {
                        fh.walletAmount = walletAction.result.amount
                    }
                }
            }
        }
        return order
    }

    static Boolean bypassEvent(Order order, OrderEvent event) {
        if (isFreeOrder(order) && event.action == OrderActionType.FULFILL.name()) {
            return true
        }
        if (event.action == OrderActionType.REFUND.name()) {
            return true
        }
        if (event.action == OrderActionType.FULFILL.name() && event.status == EventStatus.FAILED) {
            return true
        }
        return false
    }

    static String getOrderType(Order order) {
        if (CollectionUtils.isEmpty(order.orderItems)) {
            return null
        }
        Boolean hasPhysical = order.orderItems.any { OrderItem oi ->
            oi.type == ItemType.PHYSICAL_GOODS.name() || oi.type == ItemType.SHIPPING_AND_HANDLING.name()
        }
        Boolean hasDigital = order.orderItems.any { OrderItem oi ->
            oi.type != ItemType.PHYSICAL_GOODS.name() && oi.type != ItemType.SHIPPING_AND_HANDLING.name()
        }
        if (hasPhysical && hasDigital) {
            return 'bundle'
        } else if(hasPhysical) {
            return 'physical'
        }
        return 'digital'
    }
}
