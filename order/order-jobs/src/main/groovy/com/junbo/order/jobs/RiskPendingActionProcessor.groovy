package com.junbo.order.jobs
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.Transaction
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.BalanceId
import com.junbo.common.id.PaymentId
import com.junbo.common.util.IdFormatter
import com.junbo.order.spec.model.BillingHistory
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderPendingAction
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.resource.OrderResource
import com.junbo.payment.spec.enums.PaymentStatus
import com.junbo.payment.spec.model.PaymentTransaction
import com.junbo.payment.spec.resource.PaymentTransactionResource
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Created by fzhang on 2015/2/3.
 */
@CompileStatic
@Component('order.RiskPendingActionProcessor')
class RiskPendingActionProcessor implements OrderPendingActionProcessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(RiskPendingActionProcessor)

    @Resource(name = 'order.orderClient')
    OrderResource orderResource

    @Resource(name = 'order.billingBalanceClient')
    BalanceResource balanceResource

    @Resource(name = 'order.paymentTransactionClient')
    PaymentTransactionResource paymentTransactionResource

    private static Set<String> NoneRetryableRefundErrorCodes = ['133.129', '133.131'] as Set

    @Override
    boolean processPendingAction(OrderPendingAction orderPendingAction) {
        LOGGER.info('name=Start_Process_Risk_Pending_Order, order={}', IdFormatter.encodeId(orderPendingAction.orderId))
        Order order = orderResource.getOrderByOrderId(orderPendingAction.orderId).get()

        BillingHistory chargeHistory = order.billingHistories.find { BillingHistory billingHistory ->
            billingHistory.success && billingHistory.billingEvent == BillingAction.CHARGE.name()
        }
        if (chargeHistory == null) {
            throw new RuntimeException('Billing charge history not found')
        }

        Balance balance = balanceResource.getBalance(new BalanceId(Long.parseLong(chargeHistory.balanceId))).get()
        boolean hasResult = false, isReject = false
        List<String> balanceTransactionInfo = []
        balance.transactions.each { Transaction transaction ->
            PaymentTransaction paymentTransaction = paymentTransactionResource.getPayment(new PaymentId(Long.parseLong(transaction.paymentRefId))).get()
            if (paymentTransaction.status == PaymentStatus.RISK_ASYNC_REJECT.name()) {
                hasResult = true
                isReject = true
            } else if (paymentTransaction.status != PaymentStatus.RISK_PENDING.name()) {
                hasResult = true
                isReject = false
            }

            String txnInfo =  "{paymentId=${paymentTransaction.getId()}, status=${paymentTransaction.status}}"
            balanceTransactionInfo << txnInfo
        }
        LOGGER.info('name=Risk_Pending_BalanceInfo, balanceId={}, balanceTransactions:{}', IdFormatter.encodeId(balance.getId()), StringUtils.join(balanceTransactionInfo, ','))

        if (!hasResult) {
            LOGGER.info('name=RiskReview_Still_Pending, pendingActionId={}, orderId={}', orderPendingAction.getId(), orderPendingAction.orderId)
            return false
        }
        if (!isReject) {
            LOGGER.info('name=RiskReview_Approved, pendingActionId={}, orderId={}', orderPendingAction.getId(), orderPendingAction.orderId)
            return true
        }

        // rejected, do refund
        LOGGER.info('name=RiskReview_Reject, pendingActionId={}, orderId={}', orderPendingAction.getId(), orderPendingAction.orderId)
        if (isOrderRefundable(order)) {
            order.orderItems = []
            try {
                orderResource.updateOrderByOrderId(order.getId(), order)
            } catch (AppErrorException ex) {
                if (!isErrorRetryable(ex)) {
                    LOGGER.error('name=Order_Refund_Fail', ex)
                    return true
                }
                throw ex
            }
        } else {
            LOGGER.info('name=RiskReview_Reject_OrderNotRefundable, orderId={}', orderPendingAction.orderId)
        }

        return true
    }

    private boolean isOrderRefundable(Order order) {
        return !CollectionUtils.isEmpty(order.orderItems) || order.totalAmount > BigDecimal.ZERO || order.totalTax > BigDecimal.ZERO
    }

    private boolean isErrorRetryable(Exception ex) {
        if (ex instanceof AppErrorException) {
            String code = ((AppErrorException) ex).error?.error()?.code
            return code == null || !NoneRetryableRefundErrorCodes.contains(code)
        }
        return true
    }
}
