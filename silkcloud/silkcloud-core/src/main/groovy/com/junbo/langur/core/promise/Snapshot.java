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
        Thread currentThread = Thread.currentThread();
        classLoader = currentThread.getContextClassLoader();

        try {
            Object threadLocals = threadLocalsField.get(currentThread);

            if (threadLocals == null) {
                dummyThreadLocal.set("keepme");
                threadLocals = threadLocalsField.get(currentThread);
            }

            Object inheritableThreadLocals = inheritableThreadLocalsField.get(currentThread);

            if (inheritableThreadLocals == null) {
                dummyInheritableThreadLocal.set("keepme");
                inheritableThreadLocals = inheritableThreadLocalsField.get(currentThread);
            }

            assert threadLocals != null;
            assert inheritableThreadLocals != null;

            this.threadLocals = threadLocals;
            this.inheritableThreadLocals = inheritableThreadLocals;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ThreadLocal Snapshot: Stored threadlocal from tid {}, {} : {}", currentThread.getId(), threadLocals, inheritableThreadLocals);
        }
        // more things could be added. e.g. call stack, transaction context, etc.
    }

    public void resume() {
        Thread currentThread = Thread.currentThread();

        ClassLoader classLoader = currentThread.getContextClassLoader();
        if (classLoader != this.classLoader) {
            currentThread.setContextClassLoader(this.classLoader);
        }

        try {
            Object threadLocals = threadLocalsField.get(currentThread);
            if (threadLocals != this.threadLocals) {
                threadLocalsField.set(currentThread, this.threadLocals);
            }

            Object inheritableThreadLocals = inheritableThreadLocalsField.get(currentThread);
            if (inheritableThreadLocals != this.inheritableThreadLocals) {
                inheritableThreadLocalsField.set(currentThread, this.inheritableThreadLocals);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ThreadLocal Snapshot: Resumed threadlocal from tid {}, {} : {}", currentThread.getId(), threadLocals, inheritableThreadLocals);
        }
        // more things could be added. e.g. call stack, transaction context, etc.
    }
}
