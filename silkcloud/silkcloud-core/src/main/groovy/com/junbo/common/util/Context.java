/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.common.filter.SequenceIdFilter;
import com.junbo.common.routing.model.DataAccessPolicy;
import com.junbo.configuration.topo.Topology;
import com.junbo.langur.core.profiling.ProfilingHelper;
import com.junbo.langur.core.promise.ExecutorContext;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.promise.ThreadLocalRequireNew;
import groovy.lang.Closure;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

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
        private boolean isInitialRestCallBeforeClear;
        private boolean isInitialRestCall;

        private Queue<Promise> pendingTasks = new ConcurrentLinkedDeque<>();
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

        public boolean isInitialRestCallBeforeClear() {
            return isInitialRestCallBeforeClear;
        }

        public void setIsInitialRestCallBeforeClear(boolean isInitialRestCallBeforeClear) {
            this.isInitialRestCallBeforeClear = isInitialRestCallBeforeClear;
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
            ProfilingHelper.begin("CA", "execute cleanup actions");
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
                        ProfilingHelper.end("(OK)");
                        return Promise.pure();
                    }
                }).recover(new Promise.Func<Throwable, Promise<Void>>() {
                    @Override
                    public Promise<Void> apply(Throwable throwable) {
                        ProfilingHelper.err(throwable);
                        return Promise.throwing(throwable);
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
        public void registerPendingTask(final Closure<Promise> closure) {
            ProfilingHelper.begin("PT", "registering pending task");
            try {
                final String requestId = MDC.get(SequenceIdFilter.X_REQUEST_ID);
                try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                    pendingTasks.add(Promise.pure().then(new Promise.Func<Void, Promise<Void>>() {
                        @Override
                        public Promise<Void> apply(Void aVoid) {
                            MDC.put(SequenceIdFilter.X_REQUEST_ID, requestId);
                            return closure.call();
                        }
                    }, ExecutorContext.getAsyncExecutor()));
                }
                ProfilingHelper.end("(OK)");
            } catch (Throwable ex) {
                ProfilingHelper.err(ex);
                throw ex;
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
        public void registerPendingTask(final Promise.Func0<Promise> func) {
            ProfilingHelper.begin("PT", "registering pending task");
            try {
                final String requestId = MDC.get(SequenceIdFilter.X_REQUEST_ID);

                try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                    pendingTasks.add(Promise.pure().then(new Promise.Func<Void, Promise<Void>>() {
                        @Override
                        public Promise<Void> apply(Void aVoid) {
                            MDC.put(SequenceIdFilter.X_REQUEST_ID, requestId);
                            return func.apply();
                        }
                    }, ExecutorContext.getAsyncExecutor()));
                }
                ProfilingHelper.end("(OK)");
            } catch (Throwable ex) {
                ProfilingHelper.err(ex);
                throw ex;
            }
        }

        /**
         * Drains all pending tasks.
         * @return The promise that all pending tasks are drained.
         */
        public Promise<Void> drainPendingTasks() {
            ProfilingHelper.begin("PT", "draining pending task");
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
                        ProfilingHelper.end("(OK)");
                        return Promise.pure();
                    }
                }).recover(new Promise.Func<Throwable, Promise<Void>>() {
                    @Override
                    public Promise<Void> apply(Throwable throwable) {
                        ProfilingHelper.err(throwable);
                        return Promise.throwing(throwable);
                    }
                });
            }
            return future;
        }

        /**
         * Register async tasks.
         * The tasks is not in the main Promise chain of the current API call, so we used ThreadLocalRequireNew to wrap
         * the creation of the Promise. Please MAKE SURE the Promise is created in the closure passed in.
         *
         * The tasks will not be drained. Any exception in it is just logged.
         * @param closure The closure which generates the promise.
         */
        public void registerAsyncTask(final Closure<Promise> closure) {
            ProfilingHelper.begin("PT", "registering async task");
            try {
                final String requestId = MDC.get(SequenceIdFilter.X_REQUEST_ID);
                try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                    Promise.pure().then(new Promise.Func<Void, Promise<Void>>() {
                        @Override
                        public Promise<Void> apply(Void aVoid) {
                            MDC.put(SequenceIdFilter.X_REQUEST_ID, requestId);
                            try {
                                return closure.call().recover(new Promise.Func<Throwable, Promise>() {
                                    @Override
                                    public Promise apply(Throwable throwable) {
                                        logger.error("Unhandled exception in async task", throwable);
                                        return Promise.pure();
                                    }
                                });
                            } catch (Throwable throwable) {
                                logger.error("Unhandled exception in async task", throwable);
                                return Promise.pure();
                            }
                        }
                    }, ExecutorContext.getAsyncExecutor());
                }
                ProfilingHelper.end("(OK)");
            } catch (Throwable ex) {
                ProfilingHelper.err(ex);
                throw ex;
            }
        }

        /**
         * Register async tasks.
         * The tasks is not in the main Promise chain of the current API call, so we used ThreadLocalRequireNew to wrap
         * the creation of the Promise. Please MAKE SURE the Promise is created in the closure passed in.
         *
         * The tasks will not be drained. Any exception in it is just logged.
         * @param func The func which generates the promise.
         */
        public void registerAsyncTask(final Promise.Func0<Promise> func) {
            ProfilingHelper.begin("PT", "registering async task");
            try {
                final String requestId = MDC.get(SequenceIdFilter.X_REQUEST_ID);

                try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                    Promise.pure().then(new Promise.Func<Void, Promise<Void>>() {
                        @Override
                        public Promise<Void> apply(Void aVoid) {
                            MDC.put(SequenceIdFilter.X_REQUEST_ID, requestId);
                            try {
                                return func.apply().recover(new Promise.Func<Throwable, Promise>() {
                                    @Override
                                    public Promise apply(Throwable throwable) {
                                        logger.error("Unhandled exception in async task", throwable);
                                        return Promise.pure();
                                    }
                                });
                            } catch (Throwable throwable) {
                                logger.error("Unhandled exception in async task", throwable);
                                return Promise.pure();
                            }
                        }
                    }, ExecutorContext.getAsyncExecutor());
                }
                ProfilingHelper.end("(OK)");
            } catch (Throwable ex) {
                ProfilingHelper.err(ex);
                throw ex;
            }
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
