/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.common.routing.model.DataAccessPolicy;
import com.junbo.configuration.topo.Topology;
import com.junbo.langur.core.profiling.ProfilingHelper;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.promise.ThreadLocalRequireNew;
import groovy.lang.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Data utilities.
 */
public class Context {
    private Context() { }

    private static final Logger logger = LoggerFactory.getLogger(Context.class);
    private static final ThreadLocal<Data> context = new ThreadLocal<>();

    /**
     * The context of current API call.
     */
    public static class Data {
        private Integer shardId;
        private Integer dataCenterId;
        private Topology topology;
        private DataAccessPolicy dataAccessPolicy;
        private boolean isInitialRestCall;

        private List<Promise> pendingTasks = new ArrayList<>();
        private List<Closure<Promise<Void>>> cleanupActions = new ArrayList<>();

        public Integer getShardId() {
            return shardId;
        }

        public void setShardId(Integer shardId) {
            this.shardId = shardId;
        }

        public Integer getDataCenterId() {
            return dataCenterId;
        }

        public void setDataCenterId(Integer dataCenterId) {
            this.dataCenterId = dataCenterId;
        }

        public Topology getTopology() {
            return topology;
        }

        public void setTopology(Topology topology) {
            this.topology = topology;
        }

        public boolean isInitialRestCall() {
            return isInitialRestCall;
        }

        public void setIsInitialRestCall(boolean isInitialRestCall) {
            this.isInitialRestCall = isInitialRestCall;
        }

        public DataAccessPolicy getDataAccessPolicy() {
            return dataAccessPolicy;
        }

        public void setDataAccessPolicy(DataAccessPolicy dataAccessPolicy) {
            this.dataAccessPolicy = dataAccessPolicy;
            ProfilingHelper.appendRow(logger, "setDataAccessPolicy to %s", dataAccessPolicy);
        }

        /**
         * Register cleanup actions.
         * The cleanup actions are run when the request finishes. They are run before drainPendingTasks(), so they can
         * generate new pending actions.
         *
         * @param command The function run when the request finishes.
         */
        public void registerCleanupActions(Closure<Promise<Void>> command) {
            this.cleanupActions.add(command);
        }

        public Promise<Void> executeCleanupActions() {
            ProfilingHelper.appendRow(logger, "(PCA) BEGIN execute cleanup actions");
            Promise<Void> future = Promise.each(cleanupActions, new Promise.Func<Closure<Promise<Void>>, Promise>() {
                @Override
                public Promise<Void> apply(Closure<Promise<Void>> promiseClosure) {
                    return promiseClosure.call();
                }
            });
            if (ProfilingHelper.isProfileEnabled()) {
                future = future.then(new Promise.Func<Void, Promise<Void>>() {
                    @Override
                    public Promise<Void> apply(Void aVoid) {
                        ProfilingHelper.appendRow(logger, "(PCA) END execute cleanup actions");
                        return Promise.pure();
                    }
                });
            }
            return future;
        }

        /**
         * Register pending tasks.
         * The tasks is not in the main Promise chain of the current API call, so we used ThreadLocalRequireNew to wrap
         * the creation of the Promise. Please MAKE SURE the Promise is created in the closure passed in.
         *
         * The tasks will be drained automatically before returning from RestAdapter.
         * @param closure The closure which generates the promise.
         */
        public void registerPendingTask(Closure<Promise> closure) {
            ProfilingHelper.appendRow(logger, "(PT) BEGIN registering pending task");
            try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                Promise promise = closure.call();
                this.pendingTasks.add(promise);
            }
            ProfilingHelper.appendRow(logger, "(PT) END registering pending task");
        }

        /**
         * Register pending tasks.
         * The tasks is not in the main Promise chain of the current API call, so we used ThreadLocalRequireNew to wrap
         * the creation of the Promise. Please MAKE SURE the Promise is created in the closure passed in.
         *
         * The tasks will be drained automatically before returning from RestAdapter.
         * @param func The function which generates the promise.
         */
        public void registerPendingTask(Promise.Func0<Promise> func) {
            ProfilingHelper.appendRow(logger, "(PT) BEGIN registering pending task");
            try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                Promise promise = func.apply();
                this.pendingTasks.add(promise);
            }
            ProfilingHelper.appendRow(logger, "(PT) END registering pending task");
        }

        /**
         * Drains all pending tasks.
         * @return The promise that all pending tasks are drained.
         */
        public Promise<Void> drainPendingTasks() {
            ProfilingHelper.appendRow(logger, "(PT) BEGIN draining pending task");
            Promise<Void> future = Promise.each(pendingTasks, new Promise.Func<Promise, Promise>() {
                @Override
                public Promise<Void> apply(Promise promise) {
                    return promise;
                }
            });

            if (ProfilingHelper.isProfileEnabled()) {
                future = future.then(new Promise.Func<Void, Promise<Void>>() {
                    @Override
                    public Promise<Void> apply(Void aVoid) {
                        ProfilingHelper.appendRow(logger, "(PT) END draining pending task");
                        return Promise.pure();
                    }
                });
            }
            return future;
        }
    }

    public static Data get() {
        if (context.get() == null) {
            context.set(new Data());
        }
        return context.get();
    }

    public static void set(Data data) {
        context.set(data);
    }

    public static void clear() {
        context.set(null);
    }
}
