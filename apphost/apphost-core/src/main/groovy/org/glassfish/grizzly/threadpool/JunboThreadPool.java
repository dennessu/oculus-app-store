/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package org.glassfish.grizzly.threadpool;

import java.lang.reflect.Field;

/**
 * JunboThreadPool.
 */
public class JunboThreadPool extends FixedThreadPool {
    public JunboThreadPool(ThreadPoolConfig config) {
        super(config);
    }

    @Override
    protected void beforeExecute(final Worker worker, final Thread t, final Runnable r) {
        super.beforeExecute(worker, t, r);

        try {
            THREAD_LOCALS_FIELD.set(Thread.currentThread(), null);
            INHERITABLE_THREAD_LOCALS_FIELD.set(Thread.currentThread(), null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Field THREAD_LOCALS_FIELD;
    private static final Field INHERITABLE_THREAD_LOCALS_FIELD;

    static {
        try {
            THREAD_LOCALS_FIELD = Thread.class.getDeclaredField("threadLocals");
            THREAD_LOCALS_FIELD.setAccessible(true);

            INHERITABLE_THREAD_LOCALS_FIELD = Thread.class.getDeclaredField("inheritableThreadLocals");
            INHERITABLE_THREAD_LOCALS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}