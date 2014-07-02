package com.junbo.order.core.impl.common

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.order.clientproxy.model.OrderOfferItem
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.ItemType
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.order.spec.model.enums.OrderStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils

/**
 * Created by chriszhu on 3/19/14.
 */
@CompileStatic
@TypeChecked
class CoreUtils {

    static final String OFFER_ITEM_TYPE_PHYSICAL = 'PHYSICAL'
    static final String OFFER_ITEM_TYPE_STORED_VALUE = 'STORED_VALUE'


    static ItemType getOfferType(OrderOfferRevision offer) {
        // TODO support bundle type
        Boolean hasPhysical = offer.orderOfferItems?.any { OrderOfferItem item ->
            item.item.type == OFFER_ITEM_TYPE_PHYSICAL
        }

        if (hasPhysical) {
            return ItemType.PHYSICAL
        }

        Boolean hasStoredValue = offer.orderOfferItems?.any { OrderOfferItem item ->
            item.item.type == OFFER_ITEM_TYPE_STORED_VALUE
        }

        if (hasStoredValue) {
            return ItemType.STORED_VALUE
        }

        return ItemType.DIGITAL
    }

    static Boolean isFreeOrder(Order order) {
        return order.totalAmount == BigDecimal.ZERO
    }

    static Boolean hasPhysicalOffer(Order order) {
        if (CollectionUtils.isEmpty(order.orderItems)) {
            return false
        }
        if (order.orderItems.any { OrderItem oi ->
            oi.type == ItemType.PHYSICAL.name()
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
            oi.type == ItemType.STORED_VALUE.name()
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

    static Boolean checkOrderStatusCancelable(Order order) {

        // TODO: check authorized
        if (order.tentative)
            return false
        if (order.status == OrderStatus.OPEN.name() ||
                order.status == OrderStatus.PREORDERED.name() ||
                order.status == OrderStatus.PENDING_CHARGE.name()) {
            return true
        }
        return false
    }

    static Order diffRefundOrder(Order existingOrder, Order request, Integer numberAfterDecimal) {
        Order diffOrder = new Order()
        diffOrder.setId(existingOrder.getId())
        diffOrder.orderItems = []
        diffOrder.totalAmount = 0G
        diffOrder.totalTax = 0G
        diffOrder.purchaseTime = existingOrder.purchaseTime

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
                diffItem.totalTax = i.totalTax
            } else {
                if (i.quantity > requestItem.quantity) {
                    diffItem.quantity = i.quantity - requestItem.quantity
                    diffItem.totalAmount = (diffItem.quantity * i.totalAmount / i.quantity).setScale(
                            numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
                    if (i.totalAmount != BigDecimal.ZERO) {
                        diffItem.totalTax = (i.totalTax * diffItem.totalAmount / i.totalAmount).setScale(
                            numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
                    }
                    requestItem.totalAmount = requestItem.totalAmount - diffItem.totalAmount
                } else if (i.quantity == requestItem.quantity && i.totalAmount > requestItem.totalAmount) {
                    diffItem.quantity = 0
                    diffItem.totalAmount = i.totalAmount - requestItem.totalAmount
                    diffItem.totalTax = (i.totalTax * requestItem.totalAmount / i.totalAmount).setScale(
                            numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
                } else {
                    // no change
                    return
                }
            }
            diffOrder.totalAmount += diffItem.totalAmount
            diffOrder.totalTax += diffItem.totalTax
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

    static Boolean checkOrderStatusRefundable(Order order) {

        if (order.tentative)
            return false
        if (order.status == OrderStatus.CHARGED.name() ||
                order.status == OrderStatus.COMPLETED.name() ||
                order.status == OrderStatus.PARTIAL_CHARGED.name()) {
            return true
        }
        return false
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
                balance.status == BalanceStatus.PENDING_CAPTURE.name() ) {
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

    static Boolean isPreorder(OrderOfferRevision offer, String country) {
        if (offer.catalogOfferRevision.countries == null || offer.catalogOfferRevision.countries.get(country) == null) {
            return false
        }
        def releaseDate = offer.catalogOfferRevision.countries.get(country).releaseDate
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
                return order.status == OrderStatus.PENDING_CHARGE.name()
            case OrderActionType.FULFILL.name():
                return order.status == OrderStatus.PENDING_FULFILL.name()
            default:
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
        }
    }
}
