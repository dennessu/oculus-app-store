/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise.async;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.junbo.langur.core.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PromiseShell.
 */
public class AsyncPromiseShell {
    private static final Logger logger = LoggerFactory.getLogger(AsyncPromiseShell.class);

    private static int corePoolSize = 2;
    private static int maxPoolSize = 50;
    private static long keepAliveTimeMillis = 10 * 60 * 1000;   // 10 mins
    private static int queueLimit = -1;

    private static ConcurrentHashMap<String, ListeningExecutorService> pool = new ConcurrentHashMap<>();

    private AsyncPromiseShell() {
    }

    public static <R> com.junbo.langur.core.promise.Promise<R> decorate(String poolName, final Callable<R> callable) {
        return com.junbo.langur.core.promise.Promise.wrap(locate(poolName).submit(Wrapper.wrap(callable)));
    }

    @SuppressWarnings("unchecked")
    public static com.junbo.langur.core.promise.Promise<Void> decorate(String poolName, final Runnable runnable) {
        return Promise.wrap((ListenableFuture<Void>) locate(poolName).submit(Wrapper.wrap(runnable)));
    }

    private static ListeningExecutorService locate(String poolName) {
        if (!pool.containsKey(poolName)) {
            pool.putIfAbsent(poolName, MoreExecutors.listeningDecorator(new PromiseShellThreadPool(poolName)));
        }
        return pool.get(poolName);
    }

    /**
     * Customize the promise shell thread pool to set pool name and properly handle exceptions.
     */
    private static class PromiseShellThreadPool extends ThreadPoolExecutor {
        public PromiseShellThreadPool(String poolName) {
            super(corePoolSize, maxPoolSize, keepAliveTimeMillis, TimeUnit.MILLISECONDS, createQueue(queueLimit), getDefaultThreadFactory(poolName));
        }

        private static LinkedBlockingQueue createQueue(int queueLimit) {
            if (queueLimit == -1) {
                return new LinkedBlockingQueue<>();
            } else {
                return new LinkedBlockingQueue<>(queueLimit);
            }
        }

        private static ThreadFactory getDefaultThreadFactory(final String poolName) {
            final AtomicInteger counter = new AtomicInteger();

            SecurityManager s = System.getSecurityManager();
            final ThreadGroup group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();

            return new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(group, r, poolName + "(" + counter.incrementAndGet() + ")");
                    thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread t, Throwable e) {
                            logger.error("Uncaught exception in thread: " + t.getName(), e);
                        }
                    });
                    thread.setDaemon(false);
                    thread.setPriority(Thread.NORM_PRIORITY);
                    return thread;
                }
            };
        }
    }
}
