package com.junbo.order.core.impl.subledger
import com.google.common.math.IntMath
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.db.entity.enums.PayoutStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import groovy.transform.CompileStatic

import java.math.RoundingMode
/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
class SubledgerHelper {

    private static final int MONTH_A_YEAR = 12

    SubledgerRepository subledgerRepository

    OrderRepository orderRepository

    SubledgerItemContextBuilder subledgerItemContextBuilder

    private Date originTime

    private int durationInMonth

    Date getSubledgerStartTime(Date sampleTime) {
        def result = Calendar.instance
        def monthDiff = diffMonth(sampleTime, originTime)

        def deltaMonth = IntMath.divide(monthDiff, durationInMonth, RoundingMode.FLOOR) * durationInMonth
        result.setTime(originTime)
        result.add(Calendar.MONTH, deltaMonth)

        if (sampleTime.before(result.time)) {
            result.add(Calendar.MONTH, -durationInMonth)
        }

        assert result.time <= sampleTime
        return result.time
    }

    Date getNextSubledgerStartTime(Date sampleTime) {
        def result = Calendar.instance
        result.time = getSubledgerStartTime(sampleTime)
        result.add(Calendar.MONTH, durationInMonth)
        return result.time
    }

    Subledger getMatchingSubledger(SubledgerItem subledgerItem) {
        return getMatchingSubledgerByContext(subledgerItemContextBuilder.buildContext(subledgerItem))
    }

    Subledger getMatchingSubledger(OrderOffer offer, String country, String currency, Date createdTime) {
        return getMatchingSubledgerByContext(
                subledgerItemContextBuilder.buildContext(offer, country, currency, createdTime))
    }

    Subledger subledgerForItem(SubledgerItem subledgerItem) {
        def context = subledgerItemContextBuilder.buildContext(subledgerItem)
        Subledger subledger = new Subledger(
                sellerId: context.sellerId,
                offerId: context.offerId,
                startTime: getSubledgerStartTime(context.createdTime),
                endTime: getNextSubledgerStartTime(context.createdTime),
                country: context.country,
                currency: context.currency
        )
        return subledger
    }

    private Subledger getMatchingSubledgerByContext(SubledgerItemContext subledgerItemContext) {
        def sellerId = subledgerItemContext.sellerId
        def startTime = getSubledgerStartTime(subledgerItemContext.createdTime)

        return subledgerRepository.findSubledger(sellerId, PayoutStatus.PENDING.name(),
                subledgerItemContext.offerId, startTime, subledgerItemContext.currency,
                subledgerItemContext.country)
    }


    private static int diffMonth(Date left, Date right) {
        (left.year * MONTH_A_YEAR + left.month) - (right.year * MONTH_A_YEAR + right.month)
    }
}
