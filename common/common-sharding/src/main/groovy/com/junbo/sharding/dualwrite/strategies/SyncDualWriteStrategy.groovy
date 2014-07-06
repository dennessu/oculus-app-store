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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager

import javax.transaction.Transaction
import javax.transaction.TransactionManager
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
    private static final Logger logger = LoggerFactory.getLogger(SyncDualWriteStrategy.class)

    private BaseRepository repositoryImpl;
    private DualWriteQueue dualWriteQueue;
    private PendingActionReplayer replayer;
    private TransactionManager transactionManager;
    private boolean ignoreDualWriteErrors;

    private ThreadLocal<Stack<DualWriteTransactionSynchronization>> transactionStack = new ThreadLocal<>();

    public SyncDualWriteStrategy(BaseRepository repositoryImpl,
                                 PendingActionRepository pendingActionRepository,
                                 PendingActionReplayer replayer,
                                 TransactionManager transactionManager,
                                 boolean ignoreDualWriteErrors) {

        this.repositoryImpl = repositoryImpl;

        // Create a dual write queue which tracks actions in current transaction.
        // The tracked actions will be performed when transaction commits.
        this.dualWriteQueue = new DualWriteQueue(pendingActionRepository, true /* trackTransactionActions */);

        this.replayer = replayer;
        this.transactionManager = transactionManager;
        this.ignoreDualWriteErrors = ignoreDualWriteErrors;
    }

    @Override
    public Promise<CloudantEntity> invokeReadMethod(Method method, Object[] args) {
        return (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
    }

    @Override
    public Promise<CloudantEntity> invokeWriteMethod(Method method, Object[] args) {

        def synchronization = setupTransactionSynchronization();

        Promise<CloudantEntity> future = (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
        return future.then { CloudantEntity entity ->
            return synchronization.save(entity).then {
                return Promise.pure(entity);
            }
        }
    }

    @Override
    public Promise<Void> invokeDeleteMethod(Method method, Object[] args) {
        assert args.length == 1 : "Unexpected argument length: " + String.valueOf(args.length);

        def synchronization = setupTransactionSynchronization();
        return ((Promise<Void>)method.invoke(repositoryImpl, args)).then {
            return synchronization.delete(args[0]);
        }
    }

    private DualWriteTransactionSynchronization setupTransactionSynchronization() {
        if (transactionStack.get() == null) {
            transactionStack.set(new Stack<>());
        }
        def theStack = transactionStack.get();
        if (theStack.empty() || theStack.peek().isTransactionChanged()) {
            theStack.push(new DualWriteTransactionSynchronization());
            TransactionSynchronizationManager.registerSynchronization(theStack.peek());
        }

        return theStack.peek();
    }

    private class DualWriteTransactionSynchronization extends TransactionSynchronizationAdapter {
        private List<PendingAction> pendingActions;
        private Transaction transaction;

        public DualWriteTransactionSynchronization() {
            pendingActions = new ArrayList<>();
            transaction = transactionManager.transaction;

            if (transaction == null) {
                throw new RuntimeException("SyncDualWriteStrategy can only be used within transactions.");
            }
        }

        public Promise<Void> save(CloudantEntity entity) {
            return dualWriteQueue.save(entity).then { PendingAction pendingAction ->
                pendingActions.add(pendingAction);
                return Promise.pure(null);
            }
        }

        public Promise<Void> delete(Object key) {
            return dualWriteQueue.delete(key).then { PendingAction pendingAction ->
                pendingActions.add(pendingAction);
                return Promise.pure(null);
            }
        }

        public boolean isTransactionChanged() {
            return transactionManager.transaction != this.transaction;
        }

        @Override
        public void afterCommit() {
            for (final PendingAction pendingAction in pendingActions) {
                Context.get().registerPendingTask {
                    // Note: The code in this brackets are run with a new ThreadLocal
                    def future = replayer.replay(pendingAction);
                    if (ignoreDualWriteErrors) {
                        future = future.recover { Throwable ex ->
                            logger.error("Failed to dualwrite to cloudant. PendingAction ID: " + pendingAction.id);
                        }
                    }
                    return future
                }
            }
        }

        @Override
        public void afterCompletion(int status) {
            pendingActions.clear();
            transactionStack.get().pop();
        }
    }
}
