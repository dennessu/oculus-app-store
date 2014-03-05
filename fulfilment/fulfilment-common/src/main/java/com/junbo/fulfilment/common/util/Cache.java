/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.util;

import java.util.concurrent.*;

/**
 * Cache.
 */
public enum Cache {
    INSTANCE;

    private ConcurrentHashMap<String, Future> internal = new ConcurrentHashMap();

    public <V> V get(final String key, final Func<String, V> eval) {
        while (true) {
            Future<V> future = internal.get(key);

            if (future == null) {
                FutureTask<V> futureTask = new FutureTask(new Callable<V>() {
                    public V call() {
                        return eval.apply(key);
                    }
                });

                future = internal.putIfAbsent(key, futureTask);

                if (future == null) {
                    future = futureTask;
                    futureTask.run();
                }
            }

            try {
                return future.get();
            } catch (CancellationException e) {
                // avoid cache pollution
                internal.remove(key, future);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
