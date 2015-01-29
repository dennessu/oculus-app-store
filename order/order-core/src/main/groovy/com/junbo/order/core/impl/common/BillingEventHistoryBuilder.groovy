package com.junbo.order.core.impl.common

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.BillingHistory
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 14-3-26.
 */
@CompileStatic
class BillingEventHistoryBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingEventHistoryBuilder)

    static EventStatus buildEventStatusFromBalance(Balance balance) {
        def balanceStatus = BalanceStatus.valueOf(balance.status)
        switch (balanceStatus) {
            case BalanceStatus.COMPLETED:
            case BalanceStatus.AWAITING_PAYMENT:
            case BalanceStatus.PENDING_CAPTURE:
                return EventStatus.COMPLETED

            case BalanceStatus.QUEUING:
            case BalanceStatus.UNCONFIRMED:
            case BalanceStatus.INIT:
                return EventStatus.PENDING

            case BalanceStatus.CANCELLED:
            case BalanceStatus.FAILED:
                return EventStatus.FAILED
            case BalanceStatus.ERROR:
                return EventStatus.ERROR

            default:
                LOGGER.warn('name=Unknown_Balance_Status, status={}', balanceStatus)
                return EventStatus.PENDING
        }
    }

    static EventStatus buildEventStatusFromImmediateSettle(Balance balance) {
        def balanceStatus = BalanceStatus.valueOf(balance.status)
        switch (balanceStatus) {
            case BalanceStatus.COMPLETED:
            case BalanceStatus.AWAITING_PAYMENT:
            case BalanceStatus.QUEUING:
                return EventStatus.COMPLETED
            case BalanceStatus.FAILED:
                return EventStatus.FAILED
            default:
                LOGGER.warn('name=Unexpected_Balance_Status, status={}', balanceStatus)
                return EventStatus.ERROR
        }
    }

    static String buildBillingEvent(Balance balance) {
        def balanceType = BalanceType.valueOf(balance.type)
        def status = buildEventStatusFromBalance(balance)
        switch (balanceType) {
            case BalanceType.MANUAL_CAPTURE:
                if (status == EventStatus.COMPLETED) {
                    return BillingAction.AUTHORIZE.name()
                }
                return null
            case BalanceType.DEBIT:
                if (status == EventStatus.COMPLETED) {
                    return BillingAction.CHARGE.name()
                }
                else if (status == EventStatus.PENDING) {
                    return BillingAction.REQUEST_CHARGE.name()
                }
                return null
            case BalanceType.REFUND:
                if (status == EventStatus.COMPLETED) {
                    return BillingAction.REFUND.name()
                } else if (status == EventStatus.PENDING) {
                    return BillingAction.REQUEST_REFUND.name()
                }
        }
        throw new IllegalArgumentException("Balance_Type_Not_Supported, type-${balanceType}")
    }

    static BillingHistory buildBillingHistory(Balance balance) {
        def billingHistory = new BillingHistory()
        billingHistory.balanceId = (balance.id == null || balance.getId().value == null) ?
                null : balance.id.toString()
        billingHistory.totalAmount = balance.totalAmount
        if (balance.type == BalanceType.REFUND.name() || balance.type == BalanceType.CREDIT.name()) {
            billingHistory.totalAmount = 0G - balance.totalAmount
        }
        billingHistory.billingEvent = buildBillingEvent(balance)
        billingHistory.success = true
        if (balance.status == BalanceStatus.FAILED.name() || balance.status == BalanceStatus.ERROR) {
            billingHistory.success = false
        }
        return billingHistory
    }

    static BillingHistory buildBillingHistoryForImmediateSettle(Balance balance) {
        def billingHistory = new BillingHistory()
        billingHistory.balanceId = (balance.id == null || balance.getId().value == null) ?
                null : balance.id.toString()
        billingHistory.totalAmount = balance.totalAmount
        billingHistory.billingEvent = BillingAction.CHARGE
        billingHistory.success = true
        def status = buildEventStatusFromImmediateSettle(balance)

        if (status == EventStatus.COMPLETED) {
            billingHistory.success = true
        } else {
            billingHistory.success = false
        }

        return billingHistory
    }
}
