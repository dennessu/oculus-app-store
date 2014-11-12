package com.junbo.order.core.impl.subledger

import com.junbo.common.util.IdFormatter
import com.junbo.order.spec.model.FBPayoutStatusChangeRequest
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerEvent
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.PayoutStatus
import com.junbo.order.spec.model.enums.SubledgerActionType
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 2015/1/18.
 */
@CompileStatic
@Component('orderSubledgerBuilder')
class SubledgerBuilder {

    PayoutStatus buildSubledgerPayoutStatus(FBPayoutStatusChangeRequest.FBPayoutStatus fbPayoutStatus) {
        switch (fbPayoutStatus) {
            case FBPayoutStatusChangeRequest.FBPayoutStatus.HOLD:
            case FBPayoutStatusChangeRequest.FBPayoutStatus.RECEIVE:
                return PayoutStatus.PROCESSING
            case FBPayoutStatusChangeRequest.FBPayoutStatus.REJECT:
                return PayoutStatus.FAILED
            case FBPayoutStatusChangeRequest.FBPayoutStatus.PAID:
                return PayoutStatus.COMPLETED
        }
        throw new IllegalArgumentException('Unknown fbPayoutStatus.');
    }

    SubledgerEvent buildSubledgerStatusUpdateEvent(Subledger subledger, FBPayoutStatusChangeRequest fbPayoutStatusChangeRequest) {
        SubledgerEvent event = new SubledgerEvent(
                subledger: subledger.getId(),
                action: SubledgerActionType.UPDATE_STATUS.name(),
                status: EventStatus.COMPLETED.name()
        )
        event.properties = [:]
        event.properties['payoutId'] = IdFormatter.encodeId(fbPayoutStatusChangeRequest.payoutId)
        event.properties['status'] = fbPayoutStatusChangeRequest.status
        event.properties['reason'] = fbPayoutStatusChangeRequest.reason
        event.properties['fbPayoutId'] = fbPayoutStatusChangeRequest.fbPayoutId
        event.properties['financialId'] = fbPayoutStatusChangeRequest.financialId
        event.properties['startDate'] = fbPayoutStatusChangeRequest.startDate
        event.properties['endDate'] = fbPayoutStatusChangeRequest.endDate
        event.properties['payoutCurrency'] = fbPayoutStatusChangeRequest.payoutCurrency
        event.properties['payoutAmount'] = fbPayoutStatusChangeRequest.payoutAmount?.toString()
        return event
    }
}
