package com.junbo.common.job
import com.junbo.common.filter.SequenceIdFilter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import com.junbo.sharding.dualwrite.PendingActionReplayer
import com.junbo.sharding.dualwrite.data.PendingAction
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic
import org.slf4j.MDC
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
class DualWriteProcessorImpl implements DualWriteProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DualWriteProcessorImpl)

    private Map<Class, PendingActionReplayer> replayerMap = new HashMap<>()
    private PendingActionRepository repository
    private PlatformTransactionManager transactionManager

    DualWriteProcessorImpl(PlatformTransactionManager platformTransactionManager, PendingActionRepository sqlPendingActionRepository,
                       Map<Class, BaseRepository> repositoryMap) {
        repository = sqlPendingActionRepository
        transactionManager = platformTransactionManager
        repositoryMap.entrySet().each { Map.Entry<Class, BaseRepository> entry ->
            replayerMap.put(entry.key, new PendingActionReplayer(
                    entry.value,
                    sqlPendingActionRepository,
                    platformTransactionManager,
                    entry.key
            ))
        }
    }

    @Override
    Promise<Boolean> process(PendingAction pendingAction) {
        Boolean success = true;
        try {
            MDC.put(SequenceIdFilter.X_REQUEST_ID, "dualwrite-job");
            PendingActionReplayer replayer;
            String entityType;
            if (pendingAction.isDeleteAction()) {
                // delete action
                entityType = pendingAction.deletedEntityType
                replayer = replayerMap.get(loadClass(entityType))
            } else if (pendingAction.isSaveAction()) {
                entityType = pendingAction.getSavedEntity().getClass().getName()
                replayer = replayerMap.get(pendingAction.getSavedEntity().getClass())
            } else {
                pendingAction.retryCount = pendingAction.retryCount + 1
                return createInNewTran(pendingAction).then {
                    throw new IllegalStateException('Unknown action type. ID: ' + pendingAction.getId());
                }
            }
            if (replayer == null) {
                pendingAction.retryCount = pendingAction.retryCount + 1
                return createInNewTran(pendingAction).then {
                    throw new IllegalStateException('Class: ' + entityType
                            + ' has no cloudant repository configured')
                }
            }
            return replayer.replay(pendingAction).recover { Throwable t ->
                pendingAction.retryCount = pendingAction.retryCount + 1
                return createInNewTran(pendingAction).then {
                    success = false
                    LOGGER.warn(t.message)
                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(success)
            }
        } catch (Exception ex) {
            pendingAction.retryCount = pendingAction.retryCount + 1
            return createInNewTran(pendingAction).then {
                throw new IllegalStateException("Unknown exception caught: " + ex, ex);
            }
        }
    }

    Promise<PendingAction> createInNewTran(PendingAction pendingAction) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<PendingAction>>() {
            Promise<PendingAction> doInTransaction(TransactionStatus txnStatus) {
                return repository.get(pendingAction.getId()).then { PendingAction oldPendingAction ->
                    return repository.update(pendingAction, oldPendingAction)
                }
            }
        })
    }

    private static Class loadClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Failed to find class " + name, ex);
        }
    }
}
