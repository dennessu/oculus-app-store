package com.junbo.order.jobs.subledger
import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import com.junbo.order.spec.resource.SubledgerItemResource
import com.junbo.order.spec.resource.SubledgerResource
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

    @Resource(name = 'order.subledgerClient')
    SubledgerResource subledgerResource

    @Resource(name = 'order.subledgerItemClient')
    SubledgerItemResource subledgerItemResource

    @Resource(name ='orderTransactionHelper')
    TransactionHelper transactionHelper

    @Resource(name ='subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

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
                    if (subledgerItem.subledger == null) {
                        def subledgerItemContext =
                                subledgerHelper.subledgerItemContextBuilder.buildContext(subledgerItem)
                        def subledger = subledgerHelper.getMatchingSubledger(subledgerItemContext)

                        if (subledger == null) {
                            subledger = subledgerHelper.subledgerForSubledgerItemContext(subledgerItemContext)
                            subledger = subledgerResource.createSubledger(subledger).get()
                        }

                        subledgerItem.subledger = subledger.getId()
                        numItemMatchOperation++
                    }
                    subledgerItemResource.aggregateSubledgerItem(subledgerItem).get()
                }
            }

            numAggregated += subledgerItems.size()
        }

        LOGGER.info('name=Subledger_Aggregate_Job_End, numItemAggregated={}, duration={}ms, numItemMatchOperation={}',
                numAggregated, System.currentTimeMillis() - start, numItemMatchOperation)
     }

    private List<SubledgerItem> readItems() {
        def result = [] as LinkedList<SubledgerItem>
        DataCenter dataCenter = DataCenters.instance().getDataCenter(DataCenters.instance().currentDataCenterId())
        for (int shardId = 0; shardId < dataCenter.numberOfShard; ++shardId) {
            result.addAll(transactionHelper.executeInTransaction {
                return subledgerRepository.getSubledgerItem(dataCenter.id, shardId, SubledgerItemStatus.PENDING_PROCESS.name(),
                        new PageParam(count: this.pageSize, start: 0))
            })
        }
        return result
    }
}
