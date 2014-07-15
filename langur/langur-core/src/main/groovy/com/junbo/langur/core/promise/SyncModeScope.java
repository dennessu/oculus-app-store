/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import groovy.lang.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Make calls within the scope sync mode.
 */
public class SyncModeScope implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SyncModeScope.class);

    private boolean asyncModeChanged;

    public static <V> V with(Closure<V> closure) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return closure.call();
        }
    }

    public SyncModeScope() {
        setSyncMode();
    }

    /**
     * Set mode to sync if isSync = true.
     * Otherwise do nothing
     * @param isSync Whether the scope needs to be set sync
     */
    public SyncModeScope(boolean isSync) {
        if (isSync) {
            setSyncMode();
        }
    }

    @SuppressWarnings("deprecation")
    private void setSyncMode() {
        boolean oldAsyncMode = ExecutorContext.isAsyncMode();
        if (oldAsyncMode) {
            asyncModeChanged = true;
            ExecutorContext.setAsyncMode(false);
            logger.debug("Switching async mode from true to false");
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void close() {
        if (asyncModeChanged) {
            ExecutorContext.setAsyncMode(true);
            logger.debug("Switching async mode back to true", true);
        }
    }
}
