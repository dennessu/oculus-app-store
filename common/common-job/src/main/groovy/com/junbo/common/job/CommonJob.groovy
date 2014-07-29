package com.junbo.common.job

import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import com.junbo.sharding.dualwrite.data.PendingAction
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.quartz.DisallowConcurrentExecution
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback

import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by liangfu on 7/2/14.
 */
@CompileStatic
@DisallowConcurrentExecution
class CommonJob implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonJob)

    private Integer limit
    private Integer timeOffsetMillionSec
    private Integer maxThreadPoolSize
    private ThreadPoolTaskExecutor  threadPoolTaskExecutor

    private CommonProcessor dualWriteProcessor
    private PendingActionRepository pendingActionRepository
    private PlatformTransactionManager transactionManager

    void execute() {
        LOGGER.info('name=CommonProcessJobStart')
        def start = System.currentTimeMillis()
        def count = 0, numSuccess = new AtomicInteger(), numFail = new AtomicInteger()
        List<Future> futures = [] as LinkedList<Future>

        List<PendingAction> pendingActionList = readCandidates(limit, 0).get()

        while (!CollectionUtils.isEmpty(pendingActionList)) {
            pendingActionList.each { PendingAction pendingAction ->
                def future = threadPoolTaskExecutor.submit(new Callable<Void>() {
                    @Override
                    Void call() throws Exception {
                        def result = dualWriteProcessor.process(pendingAction).get()
                        if (result.success) {
                            numSuccess.andIncrement
                        } else {
                            numFail.andIncrement
                        }
                        return null
                    }
                })
                appendFuture(futures, future)
            }

            count += pendingActionList.size()
            // If the value is updated, it will remove from the view. So just need to read from the first index
            // And it won't have blocking users for some user. (Just check the vat resource)
            pendingActionList = readCandidates(limit, 0).get()
        }

        // wait all task to finish
        futures.each { Future future ->
            future.get()
        }

        LOGGER.info('name=CommonProcessJobEnd, numOfUsers={}, numSuccess={}, numFail={}, latency={}ms',
                count, numSuccess, numFail, System.currentTimeMillis() - start)
        assert count == numSuccess.get() + numFail.get()
    }

    @Override
    void afterPropertiesSet() throws Exception {
        assert dualWriteProcessor != null, 'dualWriteProcessor should not be null'
    }

    private void appendFuture(List<Future> futures, Future future) {
        futures.add(future)
        if (futures.size() >= maxThreadPoolSize) {
            def numOfFutureToRemove = maxThreadPoolSize / 2
            Iterator<Future> iterator = futures.iterator()
            for (int i = 0; i < numOfFutureToRemove; ++i) {
                iterator.next().get()
                iterator.remove()
            }
        }
    }

    Promise<List<PendingAction>> createInNewTran(int dc, int shardId, int limit, int offset) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<List<PendingAction>>>() {
            Promise<List<PendingAction>> doInTransaction(TransactionStatus txnStatus) {
                return pendingActionRepository.list(dc, shardId, limit, offset, timeOffsetMillionSec)
            }
        })
    }

    private Promise<List<PendingAction>> readCandidates(int limit, int offset) {
        DataCenter dc = DataCenters.instance().getDataCenter(DataCenters.instance().currentDataCenterId())
        if (dc == null) {
            throw new IllegalStateException('DataCenter doesn\'t exist')
        }

        List<Integer> shard = new ArrayList<>()
        for (int index = 0; index < dc.numberOfShard; index++) {
            shard.add(index)
        }

        def result = []
        return Promise.each(shard) { Integer targetShard ->
            return createInNewTran(DataCenters.instance().currentDataCenterId(), targetShard, limit, offset).then {
                List<PendingAction> pendingActionList ->
                if (!CollectionUtils.isEmpty(pendingActionList)) {
                    result.addAll(pendingActionList)
                }
                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Required
    void setLimit(Integer limit) {
        this.limit = limit
    }

    @Required
    void setMaxThreadPoolSize(Integer maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize
    }

    @Required
    void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor
    }

    @Required
    void setDualWriteProcessor(CommonProcessor dualWriteProcessor) {
        this.dualWriteProcessor = dualWriteProcessor
    }

    @Required
    void setTimeOffsetMillionSec(Integer timeOffsetMillionSec) {
        this.timeOffsetMillionSec = timeOffsetMillionSec
    }

    @Required
    void setPendingActionRepository(PendingActionRepository pendingActionRepository) {
        this.pendingActionRepository = pendingActionRepository
    }

    @Required
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }
}