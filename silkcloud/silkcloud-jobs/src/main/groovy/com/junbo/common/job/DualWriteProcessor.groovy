package com.junbo.common.job

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import com.junbo.sharding.dualwrite.PendingActionReplayer
import com.junbo.sharding.dualwrite.data.PendingAction
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionCallback

/**
 * Created by liangfu on 7/2/14.
 */
@CompileStatic
@Transactional
class DualWriteProcessor implements CommonProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DualWriteProcessor)

    private Map<Class, PendingActionReplayer> replayerMap = new HashMap<>()
    private PendingActionRepository repository
    private PlatformTransactionManager transactionManager

    DualWriteProcessor(PlatformTransactionManager platformTransactionManager, PendingActionRepository sqlPendingActionRepository,
                       Map<Class, BaseRepository> repositoryMap) {
        repository = sqlPendingActionRepository
        transactionManager = platformTransactionManager
        repositoryMap.entrySet().each { Map.Entry<Class, BaseRepository> entry ->
            replayerMap.put(entry.key, new PendingActionReplayer(
                    entry.value,
                    sqlPendingActionRepository,
                    platformTransactionManager
            ))
        }
    }

    @Override
    Promise<CommonProcessorResult> process(PendingAction pendingAction) {
        CommonProcessorResult result = new CommonProcessorResult(
                success: true
        )
        if (pendingAction.savedEntity == null) {
            pendingAction.retryCount = pendingAction.retryCount + 1
            return createInNewTran(pendingAction).then {
                throw new IllegalStateException('No Entity information found in pendingAction')
            }
        }
        PendingActionReplayer replayer = replayerMap.get(pendingAction.getSavedEntity().getClass())
        if (replayer == null) {
            pendingAction.retryCount = pendingAction.retryCount + 1
            return createInNewTran(pendingAction).then {
                throw new IllegalStateException('Class: ' + pendingAction.getSavedEntity().getClass().toString()
                        + ' has no cloudant repository configured')
            }
        }
        return replayer.replay(pendingAction).recover { Throwable t ->
            pendingAction.retryCount = pendingAction.retryCount + 1
            return createInNewTran(pendingAction).then {
                result.success = false
                LOGGER.warn(t.message)
                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(result)
        }
    }

    Promise<PendingAction> createInNewTran(PendingAction pendingAction) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<PendingAction>>() {
            Promise<PendingAction> doInTransaction(TransactionStatus txnStatus) {
                return repository.update(pendingAction, pendingAction)
            }
        })
    }
}
