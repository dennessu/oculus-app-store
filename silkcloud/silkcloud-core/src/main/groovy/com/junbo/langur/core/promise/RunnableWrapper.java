/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.junbo.configuration.ConfigServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Shenhua on 7/16/2014.
 */
public class RunnableWrapper implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RunnableWrapper.class);
    private static final ThreadLocal<Thread> tlsRunningThread = new ThreadLocal<>();
    private static boolean isDebug = "true".equalsIgnoreCase(ConfigServiceManager.instance().getConfigValue("common.conf.debugMode"));

    private final Snapshot snapshot;

    private final RunnableOnce runnable;

    public RunnableWrapper(Runnable runnable) {
        this.runnable = new RunnableOnce(runnable);
        snapshot = new Snapshot();
    }

    public Object begin() {
        Snapshot oldSnapshot = new Snapshot();
        snapshot.resume();

        return oldSnapshot;
    }

    public void end(Object oldSnapshot) {
        ((Snapshot) oldSnapshot).resume();
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
            if (isDebug) {
                Thread currentThread = Thread.currentThread();
                Thread runningThread = tlsRunningThread.get();
                if (runningThread != null && !currentThread.equals(runningThread)) {
                    // Note: this is possible if one thread is still appending the listener chain or trying to enter Looper.tryToWait(),
                    // while the IO callback is already there and asking the JunboThreadPool to schedule.
                    // In this case, one more thread is needed to process the callback, and in some extreme case it may cause a deadlock in JunboThreadPool.
                    // In such a case, the timeout in Promise.get() will recover the threads back to normal. If it really happens in heavy load, we will need
                    // a better fix, but it's not likely.

                    logger.warn("Running runnable already exists! runningThread: {}  currentThread: \n{}", runningThread.getName(), currentThread.getName());

                    // When troubleshooting, the stack traces are helpful to find out who is running.
                    // Usually it's unnecessary and we just keep the code while disabling them.
                    // StackTraceElement[] runningStack = runningThread.getStackTrace();
                    // StackTraceElement[] currentStack = currentThread.getStackTrace();
                    // String runningStackString = stackTraceToString(runningStack);
                    // String currentStackString = stackTraceToString(currentStack);
                    // logger.warn("\trunningStackTrace: \n{}\n\n\tcurrentStackTrace: \n{}", runningStackString, currentStackString);
                }
                tlsRunningThread.set(Thread.currentThread());
            }
            runnable.run();
        } finally {
            if (isDebug) {
                tlsRunningThread.remove();
            }
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

    protected static String stackTraceToString(StackTraceElement[] stackTraceElements) {
        StringBuffer buffer = new StringBuffer();
        for (StackTraceElement elem : stackTraceElements) {
            buffer.append("at ");
            buffer.append(elem.toString());
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
