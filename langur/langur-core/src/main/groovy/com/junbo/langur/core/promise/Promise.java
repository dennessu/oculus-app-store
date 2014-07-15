// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.ListenableFuture;
import com.junbo.langur.core.promise.async.AsyncPromise;
import com.junbo.langur.core.promise.sync.SyncPromise;
import groovy.lang.Closure;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by kg on 2/14/14.
 *
 * @param <T>
 */
public abstract class Promise<T> {

    public static final Object BREAK = new Object();

    /**
     * Callback0.
     */
    public static interface Callback0 {
        public void invoke();
    }

    /**
     * Callback.
     *
     * @param <A>
     */
    public static interface Callback<A> {
        public void invoke(A a);
    }

    /**
     * Func0.
     *
     * @param <R>
     */
    public static interface Func0<R> {
        public R apply();
    }

    /**
     * Func.
     *
     * @param <A>
     * @param <R>
     */
    public static interface Func<A, R> {
        public R apply(A a);
    }

    public static <T> Promise<T> wrap(ListenableFuture<T> future) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromise.wrap(future);
        } else {
            return SyncPromise.wrap(future);
        }
    }

    public static <T> Promise<T> pure(T t) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromise.pure(t);
        } else {
            return SyncPromise.pure(t);
        }
    }

    public static <T> Promise<T> throwing(Throwable throwable) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromise.throwing(throwable);
        } else {
            return SyncPromise.throwing(throwable);
        }
    }

    public static <T> Promise<T> delayed(long delay, TimeUnit unit, final Func0<T> func) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromise.delayed(delay, unit, func);
        } else {
            return SyncPromise.delayed(delay, unit, func);
        }
    }

    public static <T> Promise<T> delayed(long delay, TimeUnit unit, final Closure closure) {
        return delayed(delay, unit, new Func0<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T apply() {
                return (T) closure.call();
            }
        });
    }

    public static <T> Promise<List<T>> all(final Iterable<?> iterable, final Closure<Promise<? extends T>> closure) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromise.all(iterable, closure);
        } else {
            return SyncPromise.all(iterable, closure);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise each(final Iterator<T> iterator, final Func<? super T, Promise> func) {
        if (ExecutorContext.isAsyncMode()) {
            return AsyncPromise.each(iterator, func);
        } else {
            return SyncPromise.each(iterator, func);
        }
    }

    public static <T> T get(Closure<Promise<T>> closure) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return closure.call().syncGet();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise each(final Iterator<T> iterator, final Closure<Promise> closure) {
        return each(iterator, new Func<T, Promise>() {
            @Override
            public Promise apply(T t) {
                return closure.call(t);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise each(final Iterable<T> iterable, final Closure<Promise> closure) {
        return each(iterable == null ? null : iterable.iterator(), closure);
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise each(final Iterable<T> iterable, final Func<? super T, Promise> func) {
        return each(iterable == null ? null : iterable.iterator(), func);
    }

    /**
     * Get the result of the Promise. Can only be used in within sync mode.
     * Throws exception if AsyncPromise is called with syncGet()
     * @return the result of the promise.
     */
    public abstract T syncGet();

    /**
     * Get the result of the Promise. This is used for test purpose only.
     * In async mode, this method will block until the result is available.
     * @return the result of the promise.
     */
    public abstract T testGet();

    public abstract Promise<T> recover(final Func<Throwable, Promise<T>> func);

    public final Promise<T> recover(final Closure closure) {
        return recover(new Func<Throwable, Promise<T>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Promise<T> apply(Throwable throwable) {
                return (Promise<T>) closure.call(throwable);
            }
        });
    }

    public final Promise<T> syncRecover(final Func<Throwable, T> func) {
        return recover(new Func<Throwable, Promise<T>>() {
            @Override
            public Promise<T> apply(Throwable throwable) {
                return Promise.pure(func.apply(throwable));
            }
        });
    }

    public final Promise<T> syncRecover(final Closure closure) {
        return syncRecover(new Func<Throwable, T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T apply(Throwable throwable) {
                return (T) closure.call(throwable);
            }
        });
    }

    public final <R> Promise<R> then(final Func<? super T, Promise<R>> func) {
        return then(func, ExecutorContext.getExecutor());
    }

    public abstract <R> Promise<R> then(final Func<? super T, Promise<R>> func, final Executor executor);

    public final <R> Promise<R> then(final Closure closure) {
        return then(new Func<T, Promise<R>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Promise<R> apply(T t) {
                return (Promise<R>) closure.call(t);
            }
        });
    }

    public final <R> Promise<R> then(final Closure closure, final Executor executor) {
        return then(new Func<T, Promise<R>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Promise<R> apply(T t) {
                return (Promise<R>) closure.call(t);
            }
        }, executor);
    }

    public final <R> Promise<R> syncThen(final Func<? super T, R> func) {
        return then(new Func<T, Promise<R>>() {
            @Override
            public Promise<R> apply(T t) {
                return Promise.pure(func.apply(t));
            }
        });
    }

    public final <R> Promise<R> syncThen(final Closure closure) {
        return syncThen(new Func<T, R>() {
            @Override
            @SuppressWarnings("unchecked")
            public R apply(T t) {
                return (R) closure.call(t);
            }
        });
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public abstract void onSuccess(final Callback<? super T> action);

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public final void onSuccess(final Closure closure) {
        onSuccess(new Callback<T>() {
            @Override
            public void invoke(T t) {
                closure.call(t);
            }
        });
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public abstract void onFailure(final Callback<Throwable> action);

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public final void onFailure(final Closure closure) {
        onFailure(new Callback<Throwable>() {
            @Override
            public void invoke(Throwable throwable) {
                closure.call(throwable);
            }
        });
    }
}
