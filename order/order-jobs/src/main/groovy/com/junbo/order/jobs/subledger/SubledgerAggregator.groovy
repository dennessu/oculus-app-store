package com.junbo.order.jobs.subledger

import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.db.entity.enums.SubledgerItemStatus
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource
/**
 * Created by fzhang on 4/8/2014.
 */
class SubledgerAggregator {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerAggregator)

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    TransactionHelper transactionHelper

    SubledgerRepository subledgerRepository

    private int aggregateNumThreshHold

    private int pageSize

    void setProcessNumLimit(int processNumLimit) {
        this.processNumLimit = processNumLimit
    }

    /**
     * Method to execute periodically to do subledger aggregate in the back-end
     */
    void aggregateSubledger() {
        def start = System.currentTimeMillis()
        LOGGER.info('name=Subledger_Aggregate_Job_Start')

        def numAggregated = 0, numItemMatchOperation = 0

        while (numAggregated <= aggregateNumThreshHold) {
            def subledgerItems = subledgerRepository.getSubledgerItem(SubledgerItemStatus.PENDING.name(),
                    new PageParam(count: this.pageSize, start: 0))

            if (subledgerItems.isEmpty()) {
                break
            }

            subledgerItems.each { SubledgerItem subledgerItem ->
                transactionHelper.executeInTransaction {
                    if (subledgerItem.subledgerId == null) {
                        def subledger = subledgerHelper.getMatchingSubledger(subledgerItem)
                        if (subledger == null) {
                            subledger = createSubledgerFromItem(subledgerItem)
                        }

                        subledgerItem.subledgerId = subledger.subledgerId
                        numItemMatchOperation++
                    }
                    subledgerService.aggregateSubledgerItem(subledgerItem)
                }
            }

            numAggregated += subledgerItems.size()
        }

        LOGGER.info('name=Subledger_Aggregate_Job_End, numItemAggregated={}, duration={}ms, numItemMatchOperation={}',
                numAggregated, System.currentTimeMillis() - start, numItemMatchOperation)
     }

    Subledger createSubledgerFromItem(SubledgerItem subledgerItem) {
        def subledger = subledgerHelper.subledgerForItem(subledgerItem)
        return subledgerService.createSubledger(subledger)
    }
}
