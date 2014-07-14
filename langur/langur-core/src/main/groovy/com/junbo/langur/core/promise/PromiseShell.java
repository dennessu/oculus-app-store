/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.junbo.langur.core.promise.async.AsyncPromiseShell;
import com.junbo.langur.core.promise.sync.SyncPromiseShell;

import java.util.concurrent.Callable;

/**
 * PromiseShell.
 */
public class PromiseShell {
    private PromiseShell() { }

    public static <R> Promise<R> decorate(String poolName, final Callable<R> callable) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromiseShell.decorate(poolName, callable);
        } else {
            return SyncPromiseShell.decorate(poolName, callable);
        }
    }

    @SuppressWarnings("unchecked")
    public static Promise<Void> decorate(String poolName, final Runnable runnable) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromiseShell.decorate(poolName, runnable);
        } else {
            return SyncPromiseShell.decorate(poolName, runnable);
        }
    }
}
