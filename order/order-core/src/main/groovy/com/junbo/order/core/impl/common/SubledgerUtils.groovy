package com.junbo.order.core.impl.common

import com.junbo.order.spec.model.SubledgerAmount
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import com.junbo.order.spec.model.enums.SubledgerType
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Created by fzhang on 2015/2/3.
 */
@CompileStatic
class SubledgerUtils {

    static SubledgerAmount getSubledgerAmount(SubledgerItem subledgerItem) {
        return new SubledgerAmount(
                totalQuantity: subledgerItem.totalQuantity,
                totalPayoutAmount: subledgerItem.totalPayoutAmount,
                totalAmount: subledgerItem.totalAmount,
                taxAmount: subledgerItem.taxAmount
        )
    }

    static void updateSubledgerAmount(SubledgerAmount subledgerAmount, SubledgerItem subledgerItem) {
        subledgerItem.totalQuantity = subledgerAmount.totalQuantity
        subledgerItem.totalAmount = subledgerAmount.totalAmount
        subledgerItem.totalPayoutAmount = subledgerAmount.totalPayoutAmount
        subledgerItem.taxAmount = subledgerAmount.taxAmount
    }

    static SubledgerType getSubledgerType(SubledgerItem subledgerItem) {
        if (StringUtils.isEmpty(subledgerItem.subledgerType)) {
            return SubledgerType.PAYOUT
        }
        return SubledgerType.valueOf(subledgerItem.subledgerType)
    }

    static SubledgerItem buildSubledgerItem(SubledgerItem original, SubledgerAmount subledgerAmount) {
        SubledgerItem subledgerItem = new SubledgerItem()
        subledgerItem.item = original.item
        subledgerItem.offer = original.offer
        subledgerItem.orderItem = original.orderItem
        subledgerItem.originalSubledgerItem = original.getId()
        subledgerItem.status = SubledgerItemStatus.PENDING_PROCESS.name()

        if (subledgerAmount != null) {
            updateSubledgerAmount(subledgerAmount, subledgerItem)
        }
        return subledgerItem
    }
}
