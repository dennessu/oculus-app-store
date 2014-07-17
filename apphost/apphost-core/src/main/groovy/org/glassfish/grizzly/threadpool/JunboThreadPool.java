/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package org.glassfish.grizzly.threadpool;

import com.junbo.langur.core.promise.ExecutorContext;
import com.junbo.langur.core.promise.LocatableExecutorService;
import com.junbo.langur.core.promise.Looper;

import java.lang.reflect.Field;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * JunboThreadPool.
 */
public class JunboThreadPool extends FixedThreadPool implements LocatableExecutorService {

    private final Semaphore queuePermits;

    public JunboThreadPool(ThreadPoolConfig config) {
        super(config);

        if (config.getQueueLimit() < 0) {
            throw new IllegalArgumentException("queueLimit < 0");
        }

        queuePermits = new Semaphore(config.getQueueLimit());

        ExecutorContext.setDefaultExecutorService(this);
    }

    @Override
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
