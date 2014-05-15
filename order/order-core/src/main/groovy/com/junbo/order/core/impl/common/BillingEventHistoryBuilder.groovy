package com.junbo.order.core.impl.common

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.order.db.entity.enums.BillingAction
import com.junbo.order.db.entity.enums.EventStatus
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

            case BalanceStatus.UNCONFIRMED:
            case BalanceStatus.INIT:
                return EventStatus.PENDING

            case BalanceStatus.CANCELLED:
            case BalanceStatus.FAILED:
            case BalanceStatus.ERROR:
                return EventStatus.FAILED

            default:
                LOGGER.warn('name=Unknown_Balance_Status, status={}', balanceStatus)
                return EventStatus.PENDING
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
                    return BillingAction.PENDING_CHARGE.name()
                }
                return null
        }
        throw new IllegalArgumentException("Balance_Type_Not_Supported, type-${balanceType}")
    }

    static BillingHistory buildBillingHistory(Balance balance) {
        def billingHistory = new BillingHistory()
        billingHistory.balanceId = (balance.balanceId == null || balance.balanceId.value == null) ?
                null : balance.balanceId.value.toString()
        billingHistory.totalAmount = balance.totalAmount
        billingHistory.billingEvent = buildBillingEvent(balance)
        return billingHistory
    }

}