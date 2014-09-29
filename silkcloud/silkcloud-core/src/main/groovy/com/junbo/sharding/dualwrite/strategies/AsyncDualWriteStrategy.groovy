/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.strategies
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.DataAccessStrategy
import com.junbo.sharding.dualwrite.DualWriteQueue
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

import java.lang.reflect.Method
/**
 * DualWriteStrategy.
 *
 * In this mode, the writes to objects are tracked by the DualWriteQueue and persisted.
 * The write to secondary repository is handled asynchronously by a backend job.
 *
 */
@CompileStatic
public class AsyncDualWriteStrategy implements DataAccessStrategy {

    private BaseRepository repositoryImpl;
    private DualWriteQueue dualWriteQueue;

    public AsyncDualWriteStrategy(BaseRepository repositoryImpl, PendingActionRepository pendingActionRepository) {
        this.repositoryImpl = repositoryImpl;

        // create a dual write queue which doesn't track actions in current transaction.
        // The dual write actions will be applied by backend jobs.
        this.dualWriteQueue = new DualWriteQueue(pendingActionRepository, false /* trackTransactionActions */);
    }

    @Override
    public Promise<CloudantEntity> invokeReadMethod(Method method, Object[] args) {
        return (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
    }

    @Override
    public Promise<CloudantEntity> invokeWriteMethod(Method method, Object[] args) {
        Promise<CloudantEntity> future = (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
        return future.then { CloudantEntity entity ->
            return dualWriteQueue.save(entity).then {
                return Promise.pure(entity)
            }
        }
    }

    @Override
    public Promise<Void> invokeDeleteMethod(Method method, Object[] args) {
        assert args.length == 1 : "Unexpected argument length: " + String.valueOf(args.length);

        return ((Promise<Void>)method.invoke(repositoryImpl, args)).then {
            return dualWriteQueue.delete(args[0])
        }
    }
}
