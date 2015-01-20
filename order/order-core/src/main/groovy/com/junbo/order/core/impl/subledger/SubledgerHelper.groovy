package com.junbo.order.core.impl.subledger

import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerKey
import com.junbo.order.spec.model.enums.PayoutStatus
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
@Component('orderSubledgerHelper')
class SubledgerHelper {

    private static final long MS_A_DAY = 24L * 3600 * 1000

    @Resource(name = 'subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    @Resource(name = 'subledgerItemContextBuilder')
    SubledgerItemContextBuilder subledgerItemContextBuilder

    Date getSubledgerStartTime(Date sampleTime) {
        return new Date(sampleTime.year, sampleTime.month, sampleTime.date)
    }

    Date getNextSubledgerStartTime(Date sampleTime) {
        Date start = getSubledgerStartTime(sampleTime)
        return new Date(start.time + MS_A_DAY)
    }

    Subledger getMatchingSubledger(SubledgerKey subledgerKey) {
        def sellerId = subledgerKey.offerPublisher
        def startTime = getSubledgerStartTime(subledgerKey.subledgerTime)

        return subledgerRepository.findSubledger(sellerId, PayoutStatus.PENDING.name(),
                subledgerKey.offerId, startTime, subledgerKey.currency,
                subledgerKey.country)
    }

    Subledger subledgerForSubledgerKey(SubledgerKey subledgerKey) {
        Subledger subledger = new Subledger(
                seller: subledgerKey.offerPublisher,
                offer: subledgerKey.offerId,
                startTime: getSubledgerStartTime(subledgerKey.subledgerTime),
                endTime: getNextSubledgerStartTime(subledgerKey.subledgerTime),
                country: subledgerKey.country,
                currency: subledgerKey.currency
        )
        return subledger
    }
}
