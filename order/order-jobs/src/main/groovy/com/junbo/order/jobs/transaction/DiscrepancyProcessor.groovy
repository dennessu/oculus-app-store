package com.junbo.order.jobs.transaction
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OrderId
import com.junbo.order.core.SubledgerService
import com.junbo.order.jobs.transaction.model.DiscrepancyReason
import com.junbo.order.jobs.transaction.model.DiscrepancyRecord
import com.junbo.order.jobs.transaction.model.FacebookTransaction
import com.junbo.order.spec.model.BillingHistory
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.order.spec.model.fb.TransactionType
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.math.RoundingMode
/**
 * Created by fzhang on 2015/1/29.
 */
@CompileStatic
@Component('orderDiscrepancyProcessor')
class DiscrepancyProcessor {

    private static final int SCALE = 5

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscrepancyProcessor)

    @Resource(name = 'order.orderClient')
    OrderResource orderResource

    @Resource(name = 'order.billingBalanceClient')
    BalanceResource balanceResource

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    public DiscrepancyRecord process(OrderId orderId, FacebookTransaction transaction) {
        Order order = orderResource.getOrderByOrderId(orderId).get()
        switch (transaction.txnType) {
            case TransactionType.S:
                return handleChargeTransaction(order, transaction)
            case TransactionType.R:
                return handleRefundTransaction(order, transaction)
            default:
                createSublededgerItem(order, transaction)
                return null
        }
    }

    private DiscrepancyRecord handleChargeTransaction(Order order, FacebookTransaction transaction) {
        List<BillingHistory> chargeHistories = getSuccessBillingHistory(order, BillingAction.CHARGE)
        if (chargeHistories.isEmpty()) {
            LOGGER.info("name=Success_Charge_Not_Found, order={}", order.getId())
            return createDiscrepancyRecord(order.getId(), DiscrepancyReason.CHARGE_MISMATCH, transaction)
        }

        if (order.currency != new CurrencyId(transaction.currency)) {
            LOGGER.info("name=Order_Currency_Not_Match, order={}", order.getId())
            return createDiscrepancyRecord(order.getId(), DiscrepancyReason.CHARGE_MISMATCH, transaction)
        }

        // todo : in which scenario an order may have multiple billing history currently ?
        BigDecimal expectedAmount = 0
        chargeHistories.each { BillingHistory billingHistory ->
            expectedAmount += billingHistory.totalAmount
        }

        if (!amountEquals(expectedAmount, transaction.senderAmount)) {
            LOGGER.info("name=Order_Charge_Amount_Not_Match, order={}, expected={}, actual={}", order.getId(), expectedAmount, transaction.senderAmount)
            return createDiscrepancyRecord(order.getId(), DiscrepancyReason.CHARGE_MISMATCH, transaction)
        }
        return null
    }

    private DiscrepancyRecord handleRefundTransaction(Order order, FacebookTransaction transaction) {
        List<BillingHistory> refundHistories = getSuccessBillingHistory(order, BillingAction.REFUND)
        if (refundHistories.isEmpty()) {
            LOGGER.info("name=Success_Refund_Not_Found, order={}", order.getId())
            return createDiscrepancyRecord(order.getId(), DiscrepancyReason.REFUND_MISMATCH, transaction)
        }

        if (order.currency != new CurrencyId(transaction.currency)) {
            LOGGER.info("name=Order_Refund_Currency_Not_Match, order={}", order.getId())
            return createDiscrepancyRecord(order.getId(), DiscrepancyReason.REFUND_MISMATCH, transaction)
        }

        // check refund history has the amount
        boolean found = refundHistories.find { BillingHistory billingHistory ->
            return amountEquals(0G - billingHistory.totalAmount, transaction.senderAmount)
        } != null
        if (!found) {
            LOGGER.info("name=Success_Refund_With_Amount_NotFound, order={}, amount={}", order.getId(), transaction.senderAmount)
        }

        return null
    }

    private List<BillingHistory> getSuccessBillingHistory(Order order, BillingAction billingAction) {
        List<BillingHistory> billingHistories = []
        if (order.billingHistories == null) {
            return billingHistories
        }
        return order.billingHistories.findAll { BillingHistory billingHistory ->
            return billingHistory.billingEvent == billingAction.name() && billingHistory.success
        }.asList()
    }

    private void createSublededgerItem(Order order, FacebookTransaction transaction) {
        SubledgerType subledgerType = null
        switch (transaction.txnType) {
            case TransactionType.C:
                subledgerType = SubledgerType.CHARGE_BACK
                break
            case TransactionType.N:
                subledgerType = SubledgerType.DECLINE
                break
            case TransactionType.D:
                subledgerType = SubledgerType.CHARGE_BACK_OTW
                break
            case TransactionType.K:
                subledgerType = SubledgerType.CHARGE_BACK_REVERSAL
                break
            case TransactionType.J:
                subledgerType = SubledgerType.CHARGE_BACK_REVERSAL_OTW
                break
        }

        order.orderItems.each { OrderItem orderItem ->
            if (subledgerType == SubledgerType.CHARGE_BACK_REVERSAL || subledgerType == SubledgerType.CHARGE_BACK_REVERSAL_OTW) {
                subledgerService.createChargebackReverseSubledgerItem(subledgerType, orderItem)
            } else {
                subledgerService.createReverseSubledgerItem(subledgerType, orderItem)
            }
        }
    }

    private DiscrepancyRecord createDiscrepancyRecord(OrderId orderId, DiscrepancyReason discrepancyReason, FacebookTransaction facebookTransaction) {
        DiscrepancyRecord discrepancyRecord = new DiscrepancyRecord()
        discrepancyRecord.orderId = orderId
        discrepancyRecord.discrepancyReason = discrepancyReason
        discrepancyRecord.facebookTransaction = facebookTransaction
        return discrepancyRecord
    }

    private static boolean amountEquals(BigDecimal left, BigDecimal right) {
        left = left.setScale(SCALE, RoundingMode.FLOOR)
        right = right.setScale(SCALE, RoundingMode.FLOOR)
        return left.equals(right)
    }
}
