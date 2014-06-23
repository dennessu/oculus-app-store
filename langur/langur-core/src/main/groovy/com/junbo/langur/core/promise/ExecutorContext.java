// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * The ExecutorContext to determine which thread the then() is called.
 */
public final class ExecutorContext {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorContext.class);
    private static volatile LocatableExecutorService defaultExecutorService = null;
    private static final ThreadLocal<LocatableExecutorService> executorContext = new ThreadLocal<>();

    public static void setDefaultExecutorService(LocatableExecutorService executorService) {
        defaultExecutorService = executorService;
    }

    public static void setExecutorService(LocatableExecutorService executorService) {
        executorContext.set(executorService);
    }

    public static Executor getExecutor() {
        LocatableExecutorService executorService = executorContext.get();
        if (executorService != null) {
            if (executorService.isExecutorThread()) {
                return MoreExecutors.sameThreadExecutor();
            } else {
                return MoreExecutors.listeningDecorator(executorService);
            }
        }
        executorService = defaultExecutorService;
        if (executorService != null) {
            if (executorService.isExecutorThread()) {
                return MoreExecutors.sameThreadExecutor();
            } else {
                return MoreExecutors.listeningDecorator(executorService);
            }
        }

        logger.warn("Executor Service not set in current thread: " + Thread.currentThread().getName());
        return MoreExecutors.sameThreadExecutor();
    }
}
