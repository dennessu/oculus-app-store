package com.junbo.order.core.impl.subledger

import com.google.common.math.IntMath
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.enums.PayoutStatus
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

    @Resource(name = 'subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    @Resource(name = 'subledgerItemContextBuilder')
    SubledgerItemContextBuilder subledgerItemContextBuilder

    private Date startTime

    private int durationInMonth

    @Value('${order.subledger.starttime}')
    void setStartTime(String originTime) {
        this.startTime = new SimpleDateFormat('yyyy-MM-dd', Locale.US).parse(originTime)
    }

    @Value('${order.subledger.duration}')
    void setDurationInMonth(int durationInMonth) {
        this.durationInMonth = durationInMonth
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
        def sellerId = subledgerItemContext.seller
        def startTime = getSubledgerStartTime(subledgerItemContext.createdTime)

        return subledgerRepository.findSubledger(sellerId, PayoutStatus.PENDING.name(),
                subledgerItemContext.offer, startTime, subledgerItemContext.currency,
                subledgerItemContext.country)
    }

    Subledger getMatchingSubledger(Offer offer, CountryId country, CurrencyId currency, Date createdTime) {
        return getMatchingSubledger(
                subledgerItemContextBuilder.buildContext(offer, country, currency, createdTime))
    }

    Subledger subledgerForSubledgerItemContext(SubledgerItemContext context) {
        Subledger subledger = new Subledger(
                seller: context.seller,
                offer: context.offer,
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
