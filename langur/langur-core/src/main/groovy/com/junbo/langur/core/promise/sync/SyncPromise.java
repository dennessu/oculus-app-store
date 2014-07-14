// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise.sync;

import com.google.common.util.concurrent.ListenableFuture;
import com.junbo.langur.core.promise.Promise;
import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by kg on 2/14/14.
 *
 * @param <T>
 */
public final class SyncPromise<T> extends Promise<T> {

    public static <T> SyncPromise<T> wrap(Func0<T> func) {
        return new SyncPromise<T>(func);
    }

    public static <T> SyncPromise<T> wrap(Runnable runnable) {
        return new SyncPromise<T>(runnable);
    }

    public static <T> SyncPromise<T> wrap(Callable<T> callable) {
        return new SyncPromise<T>(callable);
    }

    public static <T> SyncPromise<T> wrap(ListenableFuture<T> future) {
        return new SyncPromise<T>(future);
    }

    public static <T> SyncPromise<T> pure(T t) {
        return new SyncPromise<>(t, null);
    }

    public static <T> SyncPromise<T> throwing(Throwable throwable) {
        return new SyncPromise<>(null, throwable);
    }

    public static <T> SyncPromise<T> delayed(final long delay, final TimeUnit unit, final Func0<T> func) {
        throw new RuntimeException("delayed is not supported in sync mode.");
    }

    public static <T> SyncPromise<List<T>> all(final Iterable<?> iterable, final Closure<Promise<? extends T>> closure) {
        List<T> result = new ArrayList<>();

        try {
            final Iterator<?> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Object item = iterator.next();
                SyncPromise<T> promise = (SyncPromise<T>) closure.call(item);
                result.add(promise.get());
            }
        } catch (Exception ex) {
            return SyncPromise.throwing(ex);
        }
        return SyncPromise.pure(result);
    }

    @SuppressWarnings("unchecked")
    public static <T> SyncPromise each(final Iterator<T> iterator, final Func<? super T, Promise> func) {
        if (iterator == null) {
            return SyncPromise.pure(null);
        }
        SyncPromise result;
        try {
            while (iterator.hasNext()) {
                T item = iterator.next();
                result = (SyncPromise)func.apply(item);
                if (result.get() == BREAK) {
                    break;
                }
            }
            return SyncPromise.pure(null);
        } catch (Exception ex) {
            return SyncPromise.throwing(ex);
        }
    }

    private T result;
    private Throwable ex;

    private SyncPromise(T result, Throwable ex) {
        this.result = result;
        this.ex = ex;
    }

    private SyncPromise(Func0<T> func) {
        try {
            this.result = func.apply();
        } catch (Exception ex) {
            this.ex = ex;
        }
    }

    private SyncPromise(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            this.ex = ex;
        }
    }

    private SyncPromise(Callable<T> callable) {
        try {
            this.result = callable.call();
        } catch (Exception ex) {
            this.ex = ex;
        }
    }

    private SyncPromise(ListenableFuture<T> future) {
        try {
            this.result = future.get();
        } catch (Exception ex) {
            this.ex = ex;
        }
    }

    public T get() {
        if (ex != null) {
            if (ex instanceof ExecutionException) {
                Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                throw new RuntimeException(cause);
            } else if (ex instanceof InterruptedException) {
                throw new RuntimeException(ex);
            } else if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            } else {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    public Promise<T> recover(final Func<Throwable, Promise<T>> func) {
        try {
            if (ex != null) {
                return func.apply(ex);
            } else {
                return this;
            }
        } catch (Exception ex) {
            return Promise.throwing(ex);
        }
    }

    public <R> Promise<R> then(final Func<? super T, Promise<R>> func, final Executor executor) {
        // ignore executor
        try {
            if (ex == null) {
                SyncPromise<R> promise = (SyncPromise<R>)func.apply(result);
                return promise;
            } else {
                return (SyncPromise<R>)this;
            }
        } catch (Exception ex) {
            return SyncPromise.throwing(ex);
        }
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public void onSuccess(final Callback<? super T> action) {
        if (ex == null) {
            action.invoke(result);
        }
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public void onFailure(final Callback<Throwable> action) {
        if (ex != null) {
            action.invoke(ex);
        }
    }
}
