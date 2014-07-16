// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise.async;

import com.google.common.util.concurrent.*;
import com.junbo.langur.core.promise.ExecutorContext;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.promise.ThreadLocalRequireNew;
import com.junbo.langur.core.promise.sync.SyncPromise;
import groovy.lang.Closure;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by kg on 2/14/14.
 *
 * @param <T>
 */
public final class AsyncPromise<T> extends Promise<T> {

    private static final Timer timer = new Timer();

    public static <T> AsyncPromise<T> wrap(ListenableFuture<T> future) {
        return new AsyncPromise<T>(future);
    }

    public static <T> AsyncPromise<T> pure(T t) {
        return wrap(Futures.immediateFuture(t));
    }

    public static <T> AsyncPromise<T> throwing(Throwable throwable) {
        return wrap(Futures.<T>immediateFailedFuture(throwable));
    }

    public static <T> AsyncPromise<T> delayed(long delay, TimeUnit unit, final Func0<T> func) {
        final Func0<T> wrapped = Wrapper.wrap(func);

        final SettableFuture<T> future = SettableFuture.create();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    future.set(wrapped.apply());
                } catch (Throwable ex) {
                    future.setException(ex);
                }
            }
        }, unit.toMillis(delay));

        return wrap(future);
    }

    public static <T> AsyncPromise<List<T>> all(final Iterable<?> iterable, final Closure<Promise<? extends T>> closure) {
        final Iterator<?> iterator = iterable.iterator();
        List<ListenableFuture<? extends T>> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                futures.add(toFuture(closure.call(item)));
            }
        }
        return wrap(Futures.allAsList(futures));
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise each(final Iterator<T> iterator, final Func<? super T, Promise> func) {
        final Func<Object, Promise> process = new Func<Object, Promise>() {
            Func<Object, Promise> self = this;

            @Override
            public Promise apply(Object result) {
                if (result == BREAK) {
                    return Promise.pure(null);
                }

                if (iterator == null || !iterator.hasNext()) {
                    return Promise.pure(null);
                }

                T item = iterator.next();

                return func.apply(item).then(self);
            }
        };

        return process.apply(null);
    }

    private final ListenableFuture<T> future;

    private AsyncPromise(ListenableFuture<T> future) {
        this.future = future;
    }

    private ListenableFuture<T> wrapped() {
        return future;
    }

    public T asyncGet() {
        try {
            return future.get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }

            throw new RuntimeException(cause);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public T testGet() {
        return asyncGet();
    }

    public T syncGet() {
        throw new RuntimeException("AsyncPromise.syncGet() is not supported.");
    }

    public Promise<T> recover(final Func<Throwable, Promise<T>> func) {
        if (!ExecutorContext.isAsyncMode()) {
            throw new RuntimeException("AsyncPromise.recover() can't be called in sync mode!");
        }
        final Func<Throwable, Promise<T>> wrapped = Wrapper.wrap(func);

        return wrap(Futures.withFallback(future, new FutureFallback<T>() {
            @Override
            public ListenableFuture<T> create(Throwable t) {
                return toFuture(wrapped.apply(t));
            }
        }));
    }

    public <R> Promise<R> then(final Func<? super T, Promise<R>> func, final Executor executor) {
        if (!ExecutorContext.isAsyncMode()) {
            throw new RuntimeException("AsyncPromise.then() can't be called in sync mode!");
        }
        final Func<? super T, Promise<R>> wrapped = Wrapper.wrap(func);

        return wrap(Futures.transform(future, new AsyncFunction<T, R>() {
            @Override
            public ListenableFuture<R> apply(T input) {
                return toFuture(wrapped.apply(input));
            }
        }, executor));
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public void onSuccess(final Callback<? super T> action) {
        final Callback<? super T> wrapped = Wrapper.wrap(action);

        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                wrapped.invoke(result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        }, ExecutorContext.getExecutor());
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public void onFailure(final Callback<Throwable> action) {
        final Callback<Throwable> wrapped = Wrapper.wrap(action);

        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
            }

            @Override
            public void onFailure(Throwable t) {
                wrapped.invoke(t);
            }
        }, ExecutorContext.getExecutor());
    }

    private static ListenableFuture toFuture(Promise promise) {
        if (promise instanceof AsyncPromise) {
            return ((AsyncPromise) promise).wrapped();
        } else if (promise instanceof SyncPromise) {
            return Futures.immediateFuture(promise.syncGet());
        }
        throw new RuntimeException("Unknown promise type: " + promise);
    }
}
