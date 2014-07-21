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
 * Create a new thread local context.
 */
public class ThreadLocalRequireNew implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalRequireNew.class);

    private static final Field threadLocalsField;
    private static final Field inheritableThreadLocalsField;

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

    private ClassLoader classLoader;

    private Object threadLocals;
    private Object inheritableThreadLocals;

    public ThreadLocalRequireNew() {
        classLoader = Thread.currentThread().getContextClassLoader();

        try {
            threadLocals = threadLocalsField.get(Thread.currentThread());
            inheritableThreadLocals = inheritableThreadLocalsField.get(Thread.currentThread());

            threadLocalsField.set(Thread.currentThread(), null);
            inheritableThreadLocalsField.set(Thread.currentThread(), null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ThreadLocal RequireNew: Forced new threadlocal from tid {}, oldtls: {} : {}",
                    Thread.currentThread().getId(), threadLocals, inheritableThreadLocals);
        }
        // more things could be added. e.g. call stack, transaction context, etc.
    }

    @Override
    public void close() {
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            threadLocalsField.set(Thread.currentThread(), threadLocals);
            inheritableThreadLocalsField.set(Thread.currentThread(), inheritableThreadLocals);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ThreadLocal RequireNew: Resumed old threadlocal from tid {}, oldtls: {} : {}",
                    Thread.currentThread().getId(), threadLocals, inheritableThreadLocals);
        }
        // more things could be added. e.g. call stack, transaction context, etc.
    }
}
