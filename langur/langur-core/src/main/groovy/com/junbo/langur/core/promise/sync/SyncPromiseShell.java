/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise.sync;

import java.util.concurrent.Callable;

/**
 * PromiseShell.
 */
public class SyncPromiseShell {
    private SyncPromiseShell() {
    }

    public static <R> com.junbo.langur.core.promise.Promise<R> decorate(String poolName, final Callable<R> callable) {
        // just run synchronously
        return SyncPromise.wrap(callable);
    }

    @SuppressWarnings("unchecked")
    public static com.junbo.langur.core.promise.Promise<Void> decorate(String poolName, final Runnable runnable) {
        // just run synchronously
        return SyncPromise.wrap(runnable);
    }
}
