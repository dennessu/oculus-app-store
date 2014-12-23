/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package org.glassfish.grizzly.threadpool;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.langur.core.promise.LocatableExecutorService;
import com.junbo.langur.core.promise.Looper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * JunboThreadPool.
 */
public class JunboThreadPool extends FixedThreadPool implements LocatableExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(JunboThreadPool.class);
    private static JunboThreadPool instance;

    private final Semaphore queuePermits;

    static {
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override public void uncaughtException(Thread t, Throwable e) {
                        logger.error("Uncaught exception in thread {}:{}", t.getId(), t.getName(), e);
                    }
                });

        int corePoolSize = AbstractThreadPool.DEFAULT_MIN_THREAD_COUNT;
        int maxPoolSize = AbstractThreadPool.DEFAULT_MAX_THREAD_COUNT;
        int keepAliveTimeMillis = AbstractThreadPool.DEFAULT_IDLE_THREAD_KEEPALIVE_TIMEOUT;
        int queueLimit = AbstractThreadPool.DEFAULT_MAX_TASKS_QUEUED;

        ConfigService configService = ConfigServiceManager.instance();
        String strMaxPoolSize = configService.getConfigValue("apphost.threadPool.maxPoolSize");
        String strKeepAliveTimeMillis = configService.getConfigValue("apphost.threadPool.keepAliveTimeMillis");
        String strQueueLimit = configService.getConfigValue("apphost.threadPool.queueLimit");

        if (!StringUtils.isEmpty(strMaxPoolSize)) {
            maxPoolSize = Integer.parseInt(strMaxPoolSize);
        }
        if (!StringUtils.isEmpty(strKeepAliveTimeMillis)) {
            keepAliveTimeMillis = Integer.parseInt(strKeepAliveTimeMillis);
        }
        if (!StringUtils.isEmpty(strQueueLimit)) {
            queueLimit = Integer.parseInt(strQueueLimit);
        }

        ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.defaultConfig();

        threadPoolConfig.poolName = "JunboThreadPool";
        threadPoolConfig.corePoolSize = corePoolSize;
        threadPoolConfig.maxPoolSize = maxPoolSize;
        threadPoolConfig.setKeepAliveTime(keepAliveTimeMillis, TimeUnit.MILLISECONDS);
        threadPoolConfig.queueLimit = queueLimit;

        instance = new JunboThreadPool(threadPoolConfig);
    }

    public static JunboThreadPool instance() {
        return instance;
    }

    public JunboThreadPool(ThreadPoolConfig config) {
        super(config);

        if (config.getQueueLimit() < 0) {
            throw new IllegalArgumentException("queueLimit < 0");
        }

        queuePermits = new Semaphore(config.getQueueLimit());
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void beforeExecute(final Worker worker, final Thread t, final Runnable r) {
        super.beforeExecute(worker, t, r);

        queuePermits.release();

        try {
            THREAD_LOCALS_FIELD.set(Thread.currentThread(), null);
            INHERITABLE_THREAD_LOCALS_FIELD.set(Thread.currentThread(), null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(Runnable command) {
        if (Looper.tryToSchedule(command)) {
            return;
        }

        if (!running) {
            throw new RejectedExecutionException("ThreadPool is not running");
        }

        if (!queuePermits.tryAcquire()) {
            onTaskQueueOverflow();
            return;
        }

        if (!workQueue.offer(command)) {
            queuePermits.release();
            onTaskQueueOverflow();
            return;
        }

        onTaskQueued(command);
    }

    @Override
    public boolean isExecutorThread() {
        String threadName = Thread.currentThread().getName();
        return threadName != null && threadName.startsWith(this.config.getPoolName() + "(");
    }

    private static final Field THREAD_LOCALS_FIELD;
    private static final Field INHERITABLE_THREAD_LOCALS_FIELD;

    static {
        try {
            THREAD_LOCALS_FIELD = Thread.class.getDeclaredField("threadLocals");
            THREAD_LOCALS_FIELD.setAccessible(true);

            INHERITABLE_THREAD_LOCALS_FIELD = Thread.class.getDeclaredField("inheritableThreadLocals");
            INHERITABLE_THREAD_LOCALS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
