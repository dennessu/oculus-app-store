package com.junbo.order.jobs.subledger

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.id.OfferId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.jobs.subledger.payout.Constants
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerKey
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import com.junbo.order.spec.resource.SubledgerItemResource
import com.junbo.order.spec.resource.SubledgerResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * Created by fzhang on 4/8/2014.
 */
@CompileStatic
@Component('subledgerAggregator')
class SubledgerAggregator {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerAggregator)

    private final static long MS_AN_HOUR = 3600 * 1000L

    private final static long MAX_SUBLEDGER_ITEM_IN_MEM= 50000

    private final static long SUBLEDGER_ITEM_AGGREGATE_THRESHOLD = 100

    private final static int MAX_RETRY_INTERVAL_IN_MS = 1000

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

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    @Resource(name = 'order.offerClient')
    private OfferResource offerResource

    @Resource(name = 'subledgerTaskAsyncTaskExecutor')
    private ThreadPoolTaskExecutor threadPoolTaskExecutor

    @Value('${order.jobs.subledger.aggregate.maxRetryCount}')
    private int maxRetryCount

    @Value('${order.jobs.subledger.aggregate.limit}')
    int aggregateNumLimit

    @Value('${order.jobs.pagesize}')
    int pageSize

    private final static SecureRandom random = new SecureRandom()

    private class AggregateStatics {
        int totalAggregated = 0;
        int totalFailed = 0;

        synchronized void aggregate(AggregateStatics statics) {
            totalAggregated += statics.totalAggregated
            totalFailed += statics.totalFailed
        }
    }

    void aggregateSubledger(String dateString) {
        aggregateSubledger(new SimpleDateFormat('yyyy-MM-dd HH:mm:ss').parse(dateString))
    }

    void aggregateSubledger() {
        aggregateSubledger(new Date())
    }

    /**
     * Method to execute periodically to do subledger aggregate in the back-end
     */
    void aggregateSubledger(Date date) {
        long start = System.currentTimeMillis()
        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
        Date endTime = new Date(((long)(date.getTime() / MS_AN_HOUR)) * MS_AN_HOUR)
        LOGGER.info('name=Subledger_Aggregate_Job_Start, endSubledgerTime={}', ObjectMapperProvider.instance().writeValueAsString(endTime))

        AggregateStatics statics = new AggregateStatics()
        DataCenter dataCenter = DataCenters.instance().getDataCenter(DataCenters.instance().currentDataCenterId())
        for (int shardId = 0; shardId < dataCenter.numberOfShard; ++shardId) {
            statics.aggregate(aggregateShard(dataCenter.id, shardId, endTime))

        }

        LOGGER.info('name=Subledger_Aggregate_Job_End, totalAggregated={}, totalFailed={}, durationInMs={}',
                statics.totalAggregated, statics.totalFailed , System.currentTimeMillis() - start)
     }

    private AggregateStatics aggregateShard(int dcId, int shardId, Date endTime) {
        long timeStarted = System.currentTimeMillis()
        AggregateStatics statics = new AggregateStatics()
        LOGGER.info('name=Subledger_Aggregate_Shard_Start, dcId={}, shardId={}', dcId, shardId)
        List<OfferId> offerIdList = listOfferId(dcId, shardId)

        // aggregate per offerId
        List<Future<Void>> tasks = [] as List
        def self = this
        offerIdList.each { OfferId offerId ->
            Future<Void> future = threadPoolTaskExecutor.submit(new Callable<Void>() {
                @Override
                Void call() throws Exception {
                    MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
                    AggregateStatics staticsPerOffer = self.aggregateByOffer(dcId, shardId, offerId, endTime)
                    statics.aggregate(staticsPerOffer)
                }
            })
            tasks << future
        }

        tasks.each { Future<Void> future ->
            future.get()
        }

        LOGGER.info('name=Subledger_Aggregate_Shard_End, dcId={}, shardId={}, totalAggregated={}, totalFailed={}, durationInMs={}',
                dcId, shardId, statics.totalAggregated, statics.totalFailed , System.currentTimeMillis() - timeStarted)
        return statics
    }

    private AggregateStatics aggregateByOffer(int dcId, int shardId, OfferId offerId, Date endTime) {
        long timeStarted = System.currentTimeMillis()
        AggregateStatics statics = new AggregateStatics()
        Map<SubledgerKey, List<SubledgerItem>> groupedSubledgerItems = [:]
        int totalInMemory = 0
        int start = 0

        LOGGER.info('name=Subledger_Aggregate_Offer_Start, dcId={}, shardId={}, offerId={}, endTime={}', dcId, shardId, offerId,
                ObjectMapperProvider.instance().writeValueAsString(endTime))
        Offer offer = offerResource.getOffer(offerId.toString()).get()

        while (true) {
            List<SubledgerItem> subledgerItemList = null
            transactionHelper.executeInNewTransaction {
                subledgerItemList = subledgerRepository.getSubledgerItem(dcId, shardId, SubledgerItemStatus.PENDING_PROCESS.name(), offerId, endTime,
                        new PageParam(count: this.pageSize, start: start))
                subledgerItemList.each { SubledgerItem subledgerItem ->
                    subledgerItem.subledgerKey = buildSubledgerKey(subledgerItem, offer, new Date(endTime.getTime() - MS_AN_HOUR))
                }
            }

            if (subledgerItemList.isEmpty()) {
                break
            }
            start += subledgerItemList.size()

            subledgerItemList.each { SubledgerItem subledgerItem ->
                if (subledgerItem.subledgerKey == null) {
                    statics.totalFailed++
                    return
                }

                List<SubledgerItem> subledgerItems = groupedSubledgerItems[subledgerItem.subledgerKey]
                if (subledgerItems == null) {
                    subledgerItems = [] as List
                    groupedSubledgerItems[subledgerItem.subledgerKey] = subledgerItems
                }
                subledgerItems << subledgerItem
                totalInMemory++

                if (subledgerItems.size() >= SUBLEDGER_ITEM_AGGREGATE_THRESHOLD) {
                    aggregateSubledgerItemsWithRetry(subledgerItems, statics)
                    totalInMemory -= subledgerItems.size()
                    subledgerItems.clear()
                } else if (totalInMemory > MAX_SUBLEDGER_ITEM_IN_MEM) {
                    groupedSubledgerItems.values().each { List<SubledgerItem> list ->
                        aggregateSubledgerItemsWithRetry(list, statics)
                    }
                    groupedSubledgerItems.clear()
                    totalInMemory = 0
                }

            }
        }

        groupedSubledgerItems.values().each { List<SubledgerItem> list ->
            aggregateSubledgerItemsWithRetry(list, statics)
        }

        LOGGER.info('name=Subledger_Aggregate_Offer_End, dcId={}, shardId={}, offerId={}, totalAggregated={}, totalFailed={}, durationInMs={}',
                dcId, shardId, offerId, statics.totalAggregated, statics.totalFailed , System.currentTimeMillis() - timeStarted)
        return statics
    }

    private List<OfferId> listOfferId(int dcId, int shardId) {
        long timeStarted = System.currentTimeMillis()
        Set<OfferId> offerIds = [] as Set
        int start = 0
        while (true) {
            List<OfferId> offerIdList = null
            transactionHelper.executeInTransaction {
                offerIdList = subledgerRepository.getDistinctSubledgerItemOfferIds(dcId, shardId, SubledgerItemStatus.PENDING_PROCESS.name(),
                        new PageParam(start: start, count: pageSize))
            }
            if (offerIdList.isEmpty()) {
                break
            }

            offerIdList.each { OfferId offerId ->
                if (offerIds.contains(offerId)) {
                    LOGGER.error('name=Distinct_Return_Duplicate_OfferId, offerId={}', offerId)
                }
            }
            offerIds.addAll(offerIdList)
            start += offerIdList.size()
        }
        List<OfferId> offerIdList = new ArrayList<OfferId>(offerIds)
        Collections.shuffle(offerIdList, random)

        LOGGER.info('name=Subledger_Aggregate_OfferIdDistinctGet, dcId={}, shardId={}, latencyInMs={}', dcId, shardId, System.currentTimeMillis() - timeStarted)
        return offerIdList
    }

    private void aggregateSubledgerItemsWithRetry(List<SubledgerItem> items, AggregateStatics statics) {
        int retryCount = 0
        while (retryCount <= maxRetryCount) {
            try {
                subledgerItemResource.aggregateSubledgerItem(items).get()
                statics.totalAggregated += items.size()
                return
            } catch (Exception ex) {
                LOGGER.error('name=Error_Aggregate_Subledger', ex)
                retryCount++
                Thread.sleep(random.nextInt(MAX_RETRY_INTERVAL_IN_MS))
            }
        }
        statics.totalFailed += items.size()
    }

    private SubledgerKey buildSubledgerKey(SubledgerItem subledgerItem, Offer offer, Date subledgerTime) {
        def orderItem = orderRepository.getOrderItem(subledgerItem.orderItem.value)
        if (orderItem == null) {
            LOGGER.error('name=Invalid_SubledgerItem,cause=orderItemNotFound,subledgerItemId={},orderItemId={}',
                    subledgerItem.getId(),subledgerItem.orderItem)
            return null
        }

        def order = orderRepository.getOrder(orderItem.orderId.value)
        if (order == null) {
            LOGGER.error('name=Invalid_SubledgerItem,cause=orderNotFound,subledgerItemId={},orderId={}',
                    subledgerItem.getId(),orderItem.orderId)
            return null
        }

        return new SubledgerKey(
                country: order.country,
                currency: order.currency,
                offerPublisher: offer.ownerId,
                offerId: new OfferId(offer.getId()),
                subledgerTime: subledgerTime
        )
    }
}
