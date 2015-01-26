package com.junbo.order.core.impl.subledger

import com.junbo.common.id.OfferId
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerCriteria
import com.junbo.order.spec.model.SubledgerKeyInfo
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

    Date getSubledgerStartTime(Date sampleTime) {
        return new Date(sampleTime.year, sampleTime.month, sampleTime.date)
    }

    Date getNextSubledgerStartTime(Date sampleTime) {
        Date start = getSubledgerStartTime(sampleTime)
        return new Date(start.time + MS_A_DAY)
    }

    Subledger getMatchingSubledger(SubledgerCriteria subledgerKey) {
        def sellerId = subledgerKey.seller
        def startTime = getSubledgerStartTime(subledgerKey.subledgerTime)

        return subledgerRepository.findSubledger(sellerId, PayoutStatus.PENDING.name(),
                subledgerKey.itemId, startTime, subledgerKey.subledgerKey, subledgerKey.currency,
                subledgerKey.country)
    }

    Subledger createSubledger(SubledgerCriteria subledgerCriteria, OfferId defaultOffer, SubledgerKeyInfo subledgerKeyInfo) {
        Subledger subledger = new Subledger(
                seller: subledgerCriteria.seller,
                offer: defaultOffer,
                item: subledgerCriteria.itemId,
                startTime: getSubledgerStartTime(subledgerCriteria.subledgerTime),
                endTime: getNextSubledgerStartTime(subledgerCriteria.subledgerTime),
                country: subledgerCriteria.country,
                currency: subledgerCriteria.currency,
                key: subledgerCriteria.subledgerKey,
                subledgerType: subledgerCriteria.subledgerType.name()
        )
        subledger.properties = subledgerKeyInfo.toProperties()
        return subledger
    }
}
