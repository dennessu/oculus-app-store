/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Shenhua on 7/16/2014.
 */
public class RunnableWrapper implements Runnable {

    private static ThreadLocal<Lock> lockThreadLocal = new ThreadLocal<Lock>() {
        @Override
        protected Lock initialValue() {
            return new ReentrantLock();
        }
    };

    private final Snapshot snapshot;

    private final RunnableOnce runnable;

    private final Lock lock;

    private final Looper looper;

    public RunnableWrapper(Runnable runnable) {
        this.runnable = new RunnableOnce(runnable);
        snapshot = new Snapshot();
        lock = lockThreadLocal.get();
        looper = Looper.current();
    }

    public Object begin() {
        Snapshot oldSnapshot = new Snapshot();
        snapshot.resume();
        return oldSnapshot;
    }

    public void end(Object oldSnapshot) {
        ((Snapshot) oldSnapshot).resume();
    }

    public Looper getLooper() {
        return looper;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public boolean hasRun() {
        return runnable.hasRun();
    }

    @Override
    public void run() {
        Object handle = begin();

        try {
            try {
                lock.lock();
                runnable.run();
            } finally {
                lock.unlock();
            }
        } finally {
            end(handle);
        }
    }

    private static class RunnableOnce implements Runnable {

        private final AtomicBoolean tag = new AtomicBoolean();

        private final Runnable runnable;

        private RunnableOnce(Runnable runnable) {
            this.runnable = runnable;
        }

        public boolean hasRun() {
            return tag.get();
        }

        @Override
        public void run() {
            if (tag.compareAndSet(false, true)) {
                runnable.run();
            }
        }
    }
}
