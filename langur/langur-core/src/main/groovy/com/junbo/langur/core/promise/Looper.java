/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Shenhua on 7/15/2014.
 */
public class Looper {
    private static final ThreadLocal<Looper> current = new ThreadLocal<Looper>() {
        @Override
        protected Looper initialValue() {
            return new Looper();
        }
    };

    public static Looper current() {
        Looper looper = current.get();
        return looper;
    }

    private final ReentrantLock lock;

    private final Condition notEmpty;

    private int startId;

    private RunnableWrapper runnableWrapper;

    public Looper() {
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
    }

    public int start() {
        lock.lock();
        try {
            startId++;

            return startId;
        } finally {
            lock.unlock();
        }

    }

    public void stop(final int startId) {
        lock.lock();
        try {
            if (this.startId != startId) {
                throw new IllegalStateException("this.startId " + this.startId + " != startId " + startId);
            }

            this.startId--;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }

    }

    public boolean offerRunnable(RunnableWrapper runnableWrapper) {
        lock.lock();
        try {
            if (this.runnableWrapper != null && !this.runnableWrapper.hasRun()) {
                throw new IllegalStateException("this.runnable != null && !this.runnableWrapper.hasRun()");
            }

            this.runnableWrapper = runnableWrapper;
            notEmpty.signal();

            if (startId == 0) {
                return false;
            }

            return true;
        } finally {
            lock.unlock();
        }

    }

    private Runnable poolRunnable(final int startId) {
        Runnable runnable = null;

        lock.lock();
        try {
            while (this.startId == startId && this.runnableWrapper == null) {
                try {
                    if (!notEmpty.await(30, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Timeout during poolRunnable");
                    }
                } catch (InterruptedException ignored) {
                    throw new RuntimeException(ignored);
                }
            }

            if (this.startId > startId) {
                throw new IllegalStateException("this.startId " + this.startId + " > startId " + startId);
            }

            if (this.runnableWrapper != null) {
                runnable = this.runnableWrapper.getRunnable();
                this.runnableWrapper = null;
            }
        } finally {
            lock.unlock();
        }

        return runnable;
    }

    public void run(int startId) {
        while (true) {
            Runnable runnable = poolRunnable(startId);

            if (runnable == null) {
                break;
            }

            runnable.run();
        }
    }

    public static void tryToWait(ListenableFuture future) {
        if (!future.isDone()) {
            final Looper looper = Looper.current();
            final int startId = looper.start();
            future.addListener(new Runnable() {
                @Override
                public void run() {
                    looper.stop(startId);
                }
            }, MoreExecutors.sameThreadExecutor());

            looper.run(startId);
        }
    }

    public static boolean tryToSchedule(Runnable command) {
        if (command instanceof RunnableWrapper) {
            RunnableWrapper wrapper = (RunnableWrapper) command;

            Object handle = wrapper.begin();
            try {
                return Looper.current().offerRunnable(wrapper);
            } finally {
                wrapper.end(handle);
            }
        }

        return false;
    }
}
