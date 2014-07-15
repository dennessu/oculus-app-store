// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * The ExecutorContext to determine which thread the then() is called.
 */
public final class ExecutorContext {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorContext.class);
    private static volatile LocatableExecutorService defaultExecutorService = null;

    private static final ThreadLocal<Boolean> isAsyncMode = new ThreadLocal<>();

    private static final Executor theExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            LocatableExecutorService executorService = defaultExecutorService;
            if (executorService != null) {
                execute(command, executorService);
                return;
            }

            logger.warn("Executor Service not set in current thread: " + Thread.currentThread().getName());
            command.run();
        }

        private void execute(Runnable command, LocatableExecutorService executorService) {
            if (executorService.isExecutorThread()) {
                command.run();
            } else {
                executorService.execute(command);
            }
        }
    };

    public static void setDefaultExecutorService(LocatableExecutorService executorService) {
        defaultExecutorService = executorService;
    }

    public static Executor getExecutor() {
        return theExecutor;
    }

    public static boolean isAsyncMode() {
        Boolean value = isAsyncMode.get();
        return value == null ? true : value.booleanValue();
    }

    /**
     * Set the async mode for current thread.
     * Set method as deprecated for restricted use.
     */
    @Deprecated
    public static void setAsyncMode(boolean isAsync) {
        isAsyncMode.set(isAsync);
    }

    @Deprecated
    public static void resetAsyncMode() {
        isAsyncMode.remove();
    }
}
