/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * PromiseShell.
 */
public enum PromiseShell {
    DEFAULT,
    PAYMENT,
    BILLING,
    CATALOG,
    SUBSCRIPTION,
    ORDER,
    IDENTITY,
    RATING,
    CART,
    FULFILMENT;

    private static final int POOL_THREADS = 50;

    private ListeningExecutorService executorService =
            MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(POOL_THREADS));

    public <R> Promise<R> decorate(final Callable<R> callable) {
        return Promise.wrap(executorService.submit(Wrapper.wrap(callable)));
    }

    public Promise<Void> decorate(final Runnable runnable) {
        return Promise.wrap((ListenableFuture<Void>) executorService.submit(Wrapper.wrap(runnable)));
    }
}
