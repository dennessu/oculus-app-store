/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
        return current.get();
    }

    private final ReentrantLock lock;

    private final Condition notEmpty;

    private volatile int nestedLevel;

    private LinkedList<RunnableWrapper> runnableWrapperQueue = new LinkedList<>();

    public Looper() {
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
    }

    public AtomicBoolean start() {
        lock.lock();
        try {
            nestedLevel++;
            return new AtomicBoolean(false);
        } finally {
            lock.unlock();
        }
    }

    public void stop(AtomicBoolean completionHolder) {
        lock.lock();
        try {
            this.nestedLevel--;
            completionHolder.set(true);

            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public boolean offerRunnable(RunnableWrapper runnableWrapper) {
        lock.lock();
        try {
            while (true) {
                RunnableWrapper wrapper = runnableWrapperQueue.peek();
                if (wrapper == null || !wrapper.hasRun()) {
                    break;
                }
                runnableWrapperQueue.poll();
            }
            runnableWrapperQueue.add(runnableWrapper);
            notEmpty.signal();

            if (nestedLevel <= 0) {
                // no thread waiting
                return false;
            }

            return true;
        } finally {
            lock.unlock();
        }

    }

    private Runnable pollRunnable(AtomicBoolean stopHolder) {
        Runnable runnable = null;

        lock.lock();
        try {
            while (!stopHolder.get() && runnableWrapperQueue.peek() == null) {
                try {
                    if (!notEmpty.await(30, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Timeout during pollRunnable");
                    }
                } catch (InterruptedException ignored) {
                    throw new RuntimeException(ignored);
                }
            }

            RunnableWrapper runnableWrapper = runnableWrapperQueue.poll();
            if (runnableWrapper != null) {
                runnable = runnableWrapper.getRunnable();
            }
        } finally {
            lock.unlock();
        }

        return runnable;
    }

    public void run(AtomicBoolean stopHolder) {
        while (true) {
            Runnable runnable = pollRunnable(stopHolder);

            if (runnable == null) {
                break;
            }

            runnable.run();
        }
    }

    public static void tryToWait(ListenableFuture future) {

        if (ExecutorContext.isExecutorThread() && !future.isDone()) {
            final Looper looper = Looper.current();
            final AtomicBoolean stopHolder = looper.start();

            future.addListener(new RawRunnable() {
                @Override
                public void run() {
                    looper.stop(stopHolder);
                }
            }, MoreExecutors.sameThreadExecutor());

            looper.run(stopHolder);
        } else if (ExecutorContext.isAsyncExecutor()) {
            throw new IllegalStateException("Cannot call .get() in AsyncThreadPool");
        }
    }

    public static boolean tryToSchedule(Runnable command) {
        if (command instanceof RunnableWrapper) {
            RunnableWrapper wrapper = (RunnableWrapper) command;

            Object handle = wrapper.begin();
            try {
                return wrapper.getLooper().offerRunnable(wrapper);
            } finally {
                wrapper.end(handle);
            }
        }

        return false;
    }
}
