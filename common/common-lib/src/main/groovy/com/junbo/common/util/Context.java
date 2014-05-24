/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.common.routing.model.DataAccessPolicy;
import com.junbo.configuration.topo.Topology;
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

    public static final String X_ROUTING_HOPS = "x-routing-hops";

    /**
     * The context of current API call.
     */
    public static class Data {
        private Integer shardId;
        private Integer dataCenterId;
        private Topology topology;
        private DataAccessPolicy dataAccessPolicy;

        private List<?> pendingActions;
        private List<Promise> pendingTasks = new ArrayList<>();

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

        public DataAccessPolicy getDataAccessPolicy() {
            return dataAccessPolicy;
        }

        public void setDataAccessPolicy(DataAccessPolicy dataAccessPolicy) {
            this.dataAccessPolicy = dataAccessPolicy;
        }

        public List<?> getPendingActions() {
            return pendingActions;
        }

        public void setPendingActions(List<?> pendingActions) {
            this.pendingActions = pendingActions;
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
            try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                Promise promise = closure.call();
                this.pendingTasks.add(promise);
            }
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
            try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                Promise promise = func.apply();
                this.pendingTasks.add(promise);
            }
        }

        /**
         * Drains all pending tasks. If there is any exception in the task, log errors and ignore.
         * @return The promise that all pending tasks are drained.
         */
        public Promise<Void> drainPendingTasks() {
            return Promise.each(pendingTasks, new Promise.Func<Promise, Promise>() {
                @Override
                public Promise<Void> apply(Promise promise) {
                    return promise.recover(new Promise.Func<Throwable, Promise<Void>>() {
                        @Override
                        public Promise<Void> apply(Throwable ex) {
                            logger.error("Exception in Context.drainTasks: " + ex);
                            return Promise.pure(null);
                        }
                    });
                }
            });
        }
    }

    public static Data get() {
        if (context.get() == null) {
            context.set(new Data());
        }
        return context.get();
    }

    public static void clear() {
        context.set(null);
    }
}
