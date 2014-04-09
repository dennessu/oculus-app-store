package com.junbo.order.jobs.subledger

import com.junbo.common.id.UserId
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.db.entity.enums.SubledgerItemAction
import com.junbo.order.db.entity.enums.SubledgerItemStatus
import com.junbo.order.db.entity.enums.PayoutStatus
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 4/8/2014.
 */
class SubledgerAggregator {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerAggregator)

    TransactionHelper transactionHelper

    CatalogFacade catalogFacade

    SubledgerService subledgerService

    SubledgerRepository subledgerRepository

    private int aggregateNumThreshHold

    private int pageSize

    void setProcessNumLimit(int processNumLimit) {
        this.processNumLimit = processNumLimit
    }

    void aggregateSubledger() {
        def start = System.currentTimeMillis()
        LOGGER.info('name=Subledger_Aggregate_Job_Start')

        def numAggregated = 0
        while (numAggregated <= aggregateNumThreshHold) {
            def subledgerItems = subledgerRepository.getSubledgerItem(SubledgerItemStatus.PENDING.name(),
                    new PageParam(count: this.pageSize, start: 0))
            if (subledgerItems.isEmpty()) {
                break
            }
            numAggregated += subledgerItems.size()
            subledgerItems.each { SubledgerItem subledgerItem ->
                transactionHelper.executeInTransaction {
                    subledgerService.aggregateSubledgerItem(subledgerItem)
                }
            }
        }

        LOGGER.info('name=Subledger_Aggregate_Job_End, numItemAggregated={}, duration={}ms',
                numAggregated, System.currentTimeMillis() - start)
     }

    void aggregateToSubledger(Subledger subledger, List<SubledgerItem> items) {
        items.each { SubledgerItem item ->

            if (item.subledgerItemAction == SubledgerItemAction.CHARGE.name()) {
                subledger.totalAmount += item.totalAmount
            }
            if (item.subledgerItemAction == SubledgerItemAction.REFUND.name()) {
                subledger.totalAmount -= item.totalAmount
            }

            item.subledgerId = subledger.subledgerId
            item.status = SubledgerItemStatus.PROCESSED
            subledgerService.updateSubledgerItem(item)
        }

        subledgerService.updateSubledger(subledger)
    }

    Subledger lookupMatchingSubledger(SubledgerItem subledgerItem, Long ownerId) {
        // get
        // catalogFacade.getOffer()
        /*def offer = catalogFacade.getOffer(item.offerId.value).wrapped().get()
        if (offer == null) {  // todo: need also get the retired offer
            LOGGER.error('name=Subledger_Offer_Not_Found, orderItemId={}, offerId={}', item.orderItemId.value,
                    item.offerId.value)
            return
        }

        offer.catalogOffer.ownerId*/
        SubledgerParam param = new SubledgerParam(
                sellerId: new UserId(ownerId),
                status: PayoutStatus.PENDING,

        )
        return null
    }
}
