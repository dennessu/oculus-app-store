/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Shenhua on 7/16/2014.
 */
public class RunnableWrapper implements Runnable {

    private final Snapshot snapshot;

    private final Runnable runnable;

    private final AtomicBoolean tag = new AtomicBoolean();

    public RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
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
        return tag.get();
    }

    @Override
    public void run() {
        if (!tag.compareAndSet(false, true)) {
            return;
        }

        Object handle = begin();
        try {
            runnable.run();
        } finally {
            end(handle);
        }
    }
}
