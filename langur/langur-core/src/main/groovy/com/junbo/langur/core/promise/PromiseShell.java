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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * PromiseShell.
 */
public class PromiseShell {
    private static final int POOL_THREADS = 50;

    private static ConcurrentHashMap<String, ListeningExecutorService> pool = new ConcurrentHashMap<>();

    public static <R> Promise<R> decorate(String poolName, final Callable<R> callable) {
        return Promise.wrap(locate(poolName).submit(Wrapper.wrap(callable)));
    }

    public static Promise<Void> decorate(String poolName, final Runnable runnable) {
        return Promise.wrap((ListenableFuture<Void>) locate(poolName).submit(Wrapper.wrap(runnable)));
    }

    private static ListeningExecutorService locate(String poolName) {
        if (!pool.containsKey(poolName)) {
            pool.putIfAbsent(poolName, MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(POOL_THREADS)));
        }

        return pool.get(poolName);
    }
}
