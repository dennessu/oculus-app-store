package com.junbo.order.core.impl.common
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.order.clientproxy.model.OrderOfferItem
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.ItemType
import com.junbo.order.spec.model.enums.OrderStatus
import com.junbo.order.spec.model.BillingHistory
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
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
            oi.type == ItemType.PHYSICAL.toString()
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
            oi.type == ItemType.STORED_VALUE.toString()
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
            balance.status == BalanceStatus.COMPLETED.name() && balance.type == BalanceType.DEBIT.name()
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
        if (balance.status == BalanceStatus.CANCELLED ||
                balance.status == BalanceStatus.ERROR ||
                balance.status == BalanceStatus.FAILED) {
            return false
        }
        return true
    }

    static Boolean checkDepositOrderRefundable(Order order, List<Balance> balances) {

        BillingHistory bh = getLatestBillingHistory(order)
        if (bh.billingEvent != BillingAction.DEPOSIT) {
            return false
        }
        // check there's only one deposit balance and the balance id is same with the billinghistory
        List<Balance> validBalances = balances.findAll { Balance ba ->
            ba.status == isValidBalance(ba)
        }.toList()

        if (validBalances != null && validBalances.size() == 1) {
            def depositBalance = validBalances[0]
            if (depositBalance.type == BalanceType.DEBIT && depositBalance.balanceId == bh.balanceId) {
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
}
