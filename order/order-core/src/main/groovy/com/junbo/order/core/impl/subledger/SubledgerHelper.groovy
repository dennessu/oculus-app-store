package com.junbo.order.core.impl.subledger

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
import java.text.SimpleDateFormat
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
}
