/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.promise;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.junbo.langur.core.promise.Promise;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * PromiseShell.
 */
public enum PromiseShell {
    DEFAULT,
    PAYMENT,
    FULFILMENT;

    private static final int POOL_THREADS = 50;

    private ListeningExecutorService executorService =
            MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(POOL_THREADS));

    public <R> Promise<R> decorate(final Callable<R> callable) {
        return Promise.wrap(executorService.submit(callable));
    }

    public Promise<Void> decorate(final Runnable runnable) {
        return Promise.wrap((ListenableFuture<Void>) executorService.submit(runnable));
    }
}
