/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.strategies
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.util.Context
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.DataAccessStrategy
import com.junbo.sharding.dualwrite.DualWriteQueue
import com.junbo.sharding.dualwrite.PendingActionReplayer
import com.junbo.sharding.dualwrite.data.PendingAction
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager

import java.lang.reflect.Method
/**
 * SyncDualWriteStrategy.
 *
 * In this mode, the writes to objects are tracked by the DualWriteQueue and persisted.
 * The write to secondary repository happens immediately after the transaction is committed.
 * There is still a backend job to write secondary repository if the write fails.
 *
 * Note: This DataAccessStrategy depends on the transaction.
 *       That means the strategy can't work properly if repositoryImpl doesn't respect the transaction.
 *
 */
@CompileStatic
public class SyncDualWriteStrategy implements DataAccessStrategy {

    private BaseRepository repositoryImpl;
    private DualWriteQueue dualWriteQueue;
    private PendingActionReplayer replayer;
    private TransactionSynchronization transactionSynchronization;

    public SyncDualWriteStrategy(BaseRepository repositoryImpl,
                                 PendingActionRepository pendingActionRepository,
                                 PendingActionReplayer replayer) {

        this.repositoryImpl = repositoryImpl;

        // Create a dual write queue which tracks actions in current transaction.
        // The tracked actions will be performed when transaction commits.
        this.dualWriteQueue = new DualWriteQueue(pendingActionRepository, true /* trackTransactionActions */);

        this.replayer = replayer;

        this.transactionSynchronization = new TransactionSynchronizationAdapter() {
            @Override
            void afterCommit() {
                for (final PendingAction pendingAction in dualWriteQueue.pendingActions) {
                    Context.get().registerPendingTask {
                        // Note: The code in this brackets are run with a new ThreadLocal
                        return replayer.replay(pendingAction);
                    }
                }
            }

            @Override
            void afterCompletion(int status) {
                dualWriteQueue.clear();
            }
        }
    }

    @Override
    public Promise<CloudantEntity> invokeReadMethod(Method method, Object[] args) {
        return (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
    }

    @Override
    public Promise<CloudantEntity> invokeWriteMethod(Method method, Object[] args) {
        setupTransactionSynchronization();
        Promise<CloudantEntity> future = (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
        return future.then { CloudantEntity entity ->
            return dualWriteQueue.save(entity).then {
                return Promise.pure(entity);
            }
        }
    }

    @Override
    public Promise<Void> invokeDeleteMethod(Method method, Object[] args) {
        assert args.length == 1 : "Unexpected argument length: " + String.valueOf(args.length);

        setupTransactionSynchronization();
        return ((Promise<Void>)method.invoke(repositoryImpl, args)).then {
            return dualWriteQueue.delete(args[0]);
        }
    }

    private void setupTransactionSynchronization() {
        // TransactionSynchronizationManager.synchronizations is a set. It's okay to add multiple times
        TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
    }
}
