package com.junbo.order.jobs.subledger.payout

import com.junbo.common.id.PayoutId

/**
 * The PayoutRecord class.
 */
class PayoutRecord {

    PayoutId payoutId

    BigDecimal payoutAmount

    Date date

    String fbPayoutId
}
