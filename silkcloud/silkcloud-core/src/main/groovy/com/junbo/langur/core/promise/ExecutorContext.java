// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import org.glassfish.grizzly.threadpool.JunboThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ExecutorContext to determine which thread the then() is called.
 */
public final class ExecutorContext {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorContext.class);
    private static volatile LocatableExecutorService defaultExecutorService = JunboThreadPool.instance();
    private static ThreadLocal<Executor> overrideExecutorService = new ThreadLocal<>();
    private static ExecutorService asyncExecutorService = createAsyncThreadPool();

    private static final Executor theExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            if (defaultExecutorService.isExecutorThread()) {
                command.run();
            } else {
                defaultExecutorService.execute(command);
            }
        }
    };

    private static final Executor asyncExecutor = new Executor() {
        @Override
        public void execute(final Runnable command) {
            String threadName = Thread.currentThread().getName();
            boolean isAsyncThread = (threadName != null && threadName.startsWith("JunboAsyncExecutorPool-"));
            if (isAsyncThread) {
                command.run();
            } else {
                asyncExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        command.run();
                    }
                });
            }
        }
    };

    public static void setDefaultExecutorService(LocatableExecutorService executorService) {
        defaultExecutorService = executorService;
    }

    public static void setAsyncExecutorService(ExecutorService executorService) {
        asyncExecutorService = executorService;
    }

    public static Executor getExecutor() {
        Executor overrideService = overrideExecutorService.get();
        if (overrideService != null) {
            return overrideService;
        } else {
            return theExecutor;
        }
    }

    public static Executor getAsyncExecutor() {
        return asyncExecutor;
    }

    public static void useAsyncExecutor() {
        overrideExecutorService.set(asyncExecutor);
    }

    public static boolean isExecutorThread() {
        return defaultExecutorService.isExecutorThread();
    }

    public static boolean isAsyncExecutor() {
        return asyncExecutor == overrideExecutorService.get();
    }

    private static ExecutorService createAsyncThreadPool() {
        ConfigService configService = ConfigServiceManager.instance();
        int asyncPoolSize = configService.getConfigValueAsInt("apphost.threadPool.asyncPoolSize", 10);
        return Executors.newFixedThreadPool(asyncPoolSize, new AsyncPoolThreadFactory());
    }

    private static class AsyncPoolThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        AsyncPoolThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    "JunboAsyncExecutorPool-" + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
