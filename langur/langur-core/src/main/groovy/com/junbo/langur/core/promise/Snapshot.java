/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Created by kg on 2/14/14.
 */
class Snapshot {
    private static final Logger logger = LoggerFactory.getLogger(Snapshot.class);

    private static final Field threadLocalsField;
    private static final Field inheritableThreadLocalsField;

    private static final ThreadLocal<String> dummyThreadLocal = new ThreadLocal<>();
    private static final InheritableThreadLocal<String> dummyInheritableThreadLocal = new InheritableThreadLocal<>();

    static {
        try {
            threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);

            inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final ClassLoader classLoader;

    private final Object threadLocals;
    private final Object inheritableThreadLocals;


    public Snapshot() {
        classLoader = Thread.currentThread().getContextClassLoader();

        // ensure the threadLocal is not null
        dummyThreadLocal.set("keepme");
        dummyInheritableThreadLocal.set("keepme");

        try {
            threadLocals = threadLocalsField.get(Thread.currentThread());
            inheritableThreadLocals = inheritableThreadLocalsField.get(Thread.currentThread());

            assert threadLocals != null;
            assert inheritableThreadLocals != null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ThreadLocal Snapshot: Stored threadlocal from tid {}, {} : {}", Thread.currentThread().getId(), threadLocals, inheritableThreadLocals);
        }
        // more things could be added. e.g. call stack, transaction context, etc.
    }

    public void resume() {
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            threadLocalsField.set(Thread.currentThread(), threadLocals);
            inheritableThreadLocalsField.set(Thread.currentThread(), inheritableThreadLocals);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ThreadLocal Snapshot: Resumed threadlocal from tid {}, {} : {}", Thread.currentThread().getId(), threadLocals, inheritableThreadLocals);
        }
        // more things could be added. e.g. call stack, transaction context, etc.
    }
}
