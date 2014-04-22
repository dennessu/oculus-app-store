package com.junbo.order.core.impl.subledger
import com.google.common.math.IntMath
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.db.entity.enums.PayoutStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.model.Subledger
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.math.RoundingMode
import java.text.SimpleDateFormat
/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
@Component('orderSubledgerHelper')
class SubledgerHelper {

    private static final int MONTH_A_YEAR = 12

    @Resource(name = 'subledgerRepository')
    SubledgerRepository subledgerRepository

    @Resource(name = 'orderRepository')
    OrderRepository orderRepository

    @Resource(name = 'subledgerItemContextBuilder')
    SubledgerItemContextBuilder subledgerItemContextBuilder

    private Date startTime

    @Value('${order.subledger.duration}')
    private int durationInMonth

    @Value('${order.subledger.starttime}')
    void setStartTime(String originTime) {
        this.startTime = new SimpleDateFormat('yyyy-MM-dd', Locale.US).parse(originTime)
    }

    Date getSubledgerStartTime(Date sampleTime) {
        def result = Calendar.instance
        def monthDiff = diffMonth(sampleTime, startTime)

        def deltaMonth = IntMath.divide(monthDiff, durationInMonth, RoundingMode.FLOOR) * durationInMonth
        result.setTime(startTime)
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

    Subledger getMatchingSubledger(SubledgerItemContext subledgerItemContext) {
        def sellerId = subledgerItemContext.sellerId
        def startTime = getSubledgerStartTime(subledgerItemContext.createdTime)

        return subledgerRepository.findSubledger(sellerId, PayoutStatus.PENDING.name(),
                subledgerItemContext.offerId, startTime, subledgerItemContext.currency,
                subledgerItemContext.country)
    }

    Subledger getMatchingSubledger(OrderOfferRevision offer, String country, String currency, Date createdTime) {
        return getMatchingSubledger(
                subledgerItemContextBuilder.buildContext(offer, country, currency, createdTime))
    }

    Subledger subledgerForSubledgerItemContext(SubledgerItemContext context) {
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

    private static int diffMonth(Date left, Date right) {
        (left.year * MONTH_A_YEAR + left.month) - (right.year * MONTH_A_YEAR + right.month)
    }
}
