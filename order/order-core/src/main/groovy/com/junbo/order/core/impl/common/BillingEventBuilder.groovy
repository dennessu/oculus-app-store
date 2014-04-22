package com.junbo.order.core.impl.common

import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.order.db.entity.enums.BillingAction
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.spec.model.BillingEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 14-3-26.
 */
class BillingEventBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingEventBuilder)

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

    static String buildEventAction(Balance balance) {
        def balanceType = BalanceType.valueOf(balance.type)
        switch (balanceType) {
            case BalanceType.CREDIT:
                return BillingAction.AUTHORIZE.name()
            case BalanceType.DEBIT:
                return BillingAction.CHARGE.name()
        }
        throw new IllegalArgumentException("Balance_Type_Not_Supported, type-${balanceType}")
    }

    static BillingEvent buildBillingEvent(Balance balance) {
        def billingEvent = new BillingEvent()
        billingEvent.balanceId = (balance.balanceId == null || balance.balanceId.value == null) ?
                null : balance.balanceId.value.toString()
        billingEvent.action = buildEventAction(balance)
        billingEvent.status = buildEventStatusFromBalance(balance)
        return billingEvent
    }

}
