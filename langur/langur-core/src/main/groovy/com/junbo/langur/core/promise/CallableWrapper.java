/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import java.util.concurrent.Callable;

/**
 * Created by Shenhua on 7/16/2014.
 */
public class CallableWrapper<V> implements Callable<V> {

    private final Snapshot snapshot;

    private final Callable<V> callable;

    public CallableWrapper(Callable<V> callable) {
        this.callable = callable;
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

    public Callable<V> getCallable() {
        return callable;
    }

    @Override
    public V call() throws Exception {
        Object handle = begin();
        try {
            return callable.call();
        } finally {
            end(handle);
        }
    }
}
