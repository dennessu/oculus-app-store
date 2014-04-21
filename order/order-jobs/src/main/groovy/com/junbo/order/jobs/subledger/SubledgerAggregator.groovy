package com.junbo.order.jobs.subledger

import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.db.entity.enums.SubledgerItemStatus
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Created by fzhang on 4/8/2014.
 */
@CompileStatic
@Component('subledgerAggregator')
class SubledgerAggregator {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerAggregator)

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    @Resource(name ='orderTransactionHelper')
    TransactionHelper transactionHelper

    @Resource(name ='subledgerRepository')
    SubledgerRepository subledgerRepository

    @Value('${order.jobs.subledger.aggregate.limit}')
    int aggregateNumLimit

    @Value('${order.jobs.pagesize}')
    int pageSize


    /**
     * Method to execute periodically to do subledger aggregate in the back-end
     */
    void aggregateSubledger() {
        def start = System.currentTimeMillis()
        LOGGER.info('name=Subledger_Aggregate_Job_Start')

        def numAggregated = 0, numItemMatchOperation = 0

        while (numAggregated <= aggregateNumLimit) {
            def subledgerItems = readItems()
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

    private List<SubledgerItem> readItems() {
        def result = [] as LinkedList<SubledgerItem>
        allShards.each { Integer shard ->
            result.addAll(transactionHelper.executeInTransaction {
                return subledgerRepository.getSubledgerItem(shard, SubledgerItemStatus.PENDING.name(),
                        new PageParam(count: this.pageSize, start: 0))
            })
        }
        return result
    }

    private List<Integer> getAllShards() {
        return [0, 1]
    }
}
