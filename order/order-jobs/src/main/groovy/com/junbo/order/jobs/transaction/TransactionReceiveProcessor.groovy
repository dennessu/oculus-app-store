package com.junbo.order.jobs.transaction
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OrderId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.core.SubledgerService
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.jobs.transaction.model.DiscrepancyReason
import com.junbo.order.jobs.transaction.model.DiscrepancyRecord
import com.junbo.order.jobs.transaction.model.FacebookTransaction
import com.junbo.order.jobs.transaction.model.TransactionProcessResult
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.*
import com.junbo.order.spec.model.fb.TransactionType
import com.junbo.order.spec.resource.OrderEventResource
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import java.math.RoundingMode
/**
 * Created by fzhang on 2015/1/29.
 */
@CompileStatic
@Component('orderTransactionReceiveProcessor')
class TransactionReceiveProcessor {

    private static final int SCALE = 5

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionReceiveProcessor)

    @Resource(name = 'order.orderClient')
    OrderResource orderResource

    @Resource(name = 'order.orderEventClient')
    OrderEventResource orderEventResource

    @Resource(name = 'order.billingBalanceClient')
    BalanceResource balanceResource

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepositoryFacade

    public void process(FacebookTransaction transaction, TransactionProcessResult transactionProcessResult) {
        LOGGER.info('name=Start_Process_TransactionReceive, orderId={}', IdFormatter.encodeId(transaction.orderId))
        Order order = orderResource.getOrderByOrderId(transaction.orderId).get()
        switch (transaction.txnType) {
            case TransactionType.S:
            case TransactionType.R:
                updateTransactionReceivePendingAction(order, transaction)
                checkChargeOrRefundTransactionDiscrepancy(order, transaction, transactionProcessResult)
                break
            case TransactionType.C:
            case TransactionType.D:
                handleChargeBack(order, transaction)
                break
            default:
                createSublededgerItem(order, transaction)
        }
    }

    private DiscrepancyRecord checkChargeOrRefundTransactionDiscrepancy(Order order, FacebookTransaction transaction, TransactionProcessResult transactionProcessResult) {
        Set<String> billingActions = transaction.txnType == TransactionType.S ? [BillingAction.CHARGE.name()] as Set :
                [BillingAction.REFUND.name(), BillingAction.REFUND_TAX.name()] as Set
        List<BillingHistory> billingHistories = order.billingHistories.findAll { BillingHistory billingHistory ->
            return billingActions.contains(billingHistory.billingEvent) && billingHistory.success
        }.asList()
        DiscrepancyReason discrepancyReason

        if (CollectionUtils.isEmpty(billingHistories)) {
            discrepancyReason = DiscrepancyReason.BILLING_HISTORY_NOT_FOUND
        }else if (order.currency != new CurrencyId(transaction.currency)) {
            discrepancyReason = DiscrepancyReason.CURRENCY_NOT_MATCH
        } else {
            BigDecimal expected = transaction.senderAmount
            if (transaction.txnType == TransactionType.R) {
                expected = -expected
            }
            if (!billingHistories.any {BillingHistory bh -> amountEquals(bh.totalAmount, expected)}) {
                discrepancyReason = DiscrepancyReason.BILLING_HISTORY_AMOUNT_NOT_MATCH
            }
        }

        if (discrepancyReason == null) {
            return null
        }

        LOGGER.error('name=Transaction_Discrepancy_Detected, orderId={}, fbTxnType={}, fbPaymentId={}, reason={}', IdFormatter.encodeId(transaction.orderId),
                transaction.txnType, transaction.fbPaymentId, discrepancyReason)
        transactionProcessResult.discrepancyRecord = createDiscrepancyRecord(order.getId(), discrepancyReason, transaction)
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

    private void updateTransactionReceivePendingAction(Order order, FacebookTransaction transaction) {
        Assert.isTrue(transaction.txnType == TransactionType.S || transaction.txnType == TransactionType.R)
        BalanceType balanceType = transaction.txnType == TransactionType.S ? BalanceType.DEBIT : BalanceType.REFUND
        transactionHelper.executeInTransaction {
            List<OrderPendingAction> orderPendingActionList = orderRepositoryFacade.getOrderPendingActionsByOrderId(order.getId(), OrderPendingActionType.PAYMENT_TRANSACTION_RECEIVE)
            OrderPendingAction orderPendingAction = orderPendingActionList.find { OrderPendingAction orderPendingAction ->
                orderPendingAction.properties?.get(OrderPendingAction.PropertyKey.balanceType) == balanceType.name()
            }

            if (orderPendingAction == null) {
                LOGGER.error('name=TransactionReceiveOrderPendingActionNotFound, orderId={}, balanceType={}, fbPaymentId={}', order.getId(), balanceType, transaction.fbPaymentId)
            } else {
                LOGGER.info('name=Mark_Complete_TransactionReceive_OrderPendingAction, orderId={}, orderPendingActionId={}', IdFormatter.encodeId(order.getId()), orderPendingAction.getId())
                orderPendingAction.completed = true
                orderRepositoryFacade.updateOrderPendingAction(orderPendingAction)
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
