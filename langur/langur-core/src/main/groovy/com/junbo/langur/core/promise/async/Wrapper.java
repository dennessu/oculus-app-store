/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.promise.async;

import com.junbo.langur.core.promise.Promise;

import java.util.concurrent.Callable;

/**
 * Created by kg on 2/14/14.
 */
class Wrapper {

    private Wrapper() {

    }

    public static Promise.Callback0 wrap(final Promise.Callback0 callback0) {
        return new Promise.Callback0() {
            final Snapshot snapshot = new Snapshot();

            @Override
            public void invoke() {
                Snapshot oldSnapshot = new Snapshot();
                snapshot.resume();
                try {
                    callback0.invoke();
                } finally {
                    oldSnapshot.resume();
                }
            }
        };
    }

    public static <A> Promise.Callback<A> wrap(final Promise.Callback<A> callback) {
        return new Promise.Callback<A>() {
            final Snapshot snapshot = new Snapshot();

            @Override
            public void invoke(A a) {
                Snapshot oldSnapshot = new Snapshot();
                snapshot.resume();
                try {
                    callback.invoke(a);
                } finally {
                    oldSnapshot.resume();
                }
            }
        };
    }

    public static <R> Promise.Func0<R> wrap(final Promise.Func0<R> func) {
        return new Promise.Func0<R>() {
            final Snapshot snapshot = new Snapshot();

            @Override
            public R apply() {
                Snapshot oldSnapshot = new Snapshot();
                snapshot.resume();
                try {
                    return func.apply();
                } finally {
                    oldSnapshot.resume();
                }
            }
        };
    }

    public static <A, R> Promise.Func<A, R> wrap(final Promise.Func<A, R> func) {
        return new Promise.Func<A, R>() {
            final Snapshot snapshot = new Snapshot();

            @Override
            public R apply(A a) {
                Snapshot oldSnapshot = new Snapshot();
                snapshot.resume();
                try {
                    return func.apply(a);
                } finally {
                    oldSnapshot.resume();
                }
            }
        };
    }

    public static <R> Callable<R> wrap(final Callable<R> callable) {
        return new Callable<R>() {
            final Snapshot snapshot = new Snapshot();

            public R call() throws Exception {
                Snapshot oldSnapshot = new Snapshot();
                snapshot.resume();
                try {
                    return callable.call();
                } finally {
                    oldSnapshot.resume();
                }
            }
        };
    }

    public static Runnable wrap(final Runnable runnable) {
        return new Runnable() {
            final Snapshot snapshot = new Snapshot();

            public void run() {
                Snapshot oldSnapshot = new Snapshot();
                snapshot.resume();
                try {
                    runnable.run();
                } finally {
                    oldSnapshot.resume();
                }
            }
        };
    }
}
