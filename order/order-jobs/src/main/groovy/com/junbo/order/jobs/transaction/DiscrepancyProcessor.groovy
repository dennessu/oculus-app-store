package com.junbo.order.jobs.transaction
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OrderId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.order.core.SubledgerService
import com.junbo.order.jobs.transaction.model.DiscrepancyReason
import com.junbo.order.jobs.transaction.model.DiscrepancyRecord
import com.junbo.order.jobs.transaction.model.FacebookTransaction
import com.junbo.order.spec.model.BillingHistory
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.order.spec.model.fb.TransactionType
import com.junbo.order.spec.resource.OrderEventResource
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.Assert

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

    @Resource(name = 'order.orderEventClient')
    OrderEventResource orderEventResource

    @Resource(name = 'order.billingBalanceClient')
    BalanceResource balanceResource

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    public DiscrepancyRecord process(OrderId orderId, FacebookTransaction transaction) {
        LOGGER.info('name=Start_Check_Discrepancy, orderId={}', IdFormatter.encodeId(orderId))
        Order order = orderResource.getOrderByOrderId(orderId).get()
        switch (transaction.txnType) {
            case TransactionType.S:
                return handleChargeTransaction(order, transaction)
            case TransactionType.R:
                return handleRefundTransaction(order, transaction)
            case TransactionType.C:
            case TransactionType.D:
                handleChargeBack(order, transaction)
                return null
            default:
                createSublededgerItem(order, transaction)
                return null
        }
    }

    private DiscrepancyRecord handleChargeTransaction(Order order, FacebookTransaction transaction) {
        List<BillingHistory> chargeHistories = getSuccessBillingHistory(order, BillingAction.CHARGE)
        LOGGER.info('name=Check_ChargeTransaction_Discrepancy, orderId={}', IdFormatter.encodeId(order.getId()))

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
        LOGGER.info('name=Check_RefundTransaction_Discrepancy, orderId={}', IdFormatter.encodeId(order.getId()))

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
            case TransactionType.N:
                subledgerType = SubledgerType.DECLINE
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

    private void handleChargeBack(Order order, FacebookTransaction transaction) {
        Assert.isTrue(transaction.txnType == TransactionType.C || transaction.txnType == TransactionType.D)
        LOGGER.info('name=Start_Post_ChargeBack_OrderEvent, orderId={}', IdFormatter.encodeId(order.getId()))
        try {
            OrderEvent orderEvent = new OrderEvent(
                    action: OrderActionType.CHARGE_BACK.name(),
                    status: EventStatus.OPEN.name(),
                    order: order.getId()
            )
            if (transaction.txnType == TransactionType.D) {
                orderEvent.properties = ObjectMapperProvider.instance().writeValueAsString(Collections.singletonMap('outsideOfTimeWindow',true))
            }

            OrderEvent result = orderEventResource.createOrderEvent(orderEvent).get()
            LOGGER.info('name=Post_ChargeBack_OrderEvent_Success, orderId={}, orderEventId={}', IdFormatter.encodeId(order.getId()), IdFormatter.encodeId(result.getId()))
        } catch (Exception ex) {
            LOGGER.error('name=Post_ChargeBack_OrderEvent_Fail, orderId={}', IdFormatter.encodeId(order.getId()), ex)
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
