/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import java.lang.reflect.Field;

/**
 * Created by kg on 2/14/14.
 */
class Snapshot {

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

    private final ClassLoader classLoader;

    private final Object threadLocals;
    private final Object inheritableThreadLocals;


    public Snapshot() {
        classLoader = Thread.currentThread().getContextClassLoader();

        try {
            threadLocals = threadLocalsField.get(Thread.currentThread());
            inheritableThreadLocals = inheritableThreadLocalsField.get(Thread.currentThread());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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

        // more things could be added. e.g. call stack, transaction context, etc.
    }
}
