// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.*;
import groovy.lang.Closure;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AsyncPromise doesn't grab threadlocal from current thread.
 * This class repeats the interface of Promise, and adds a join() method.
 *
 * asyncPromise.join(promise) means when the asyncPromise finishes, continue with the promise.
 * This function returns a promise.
 *
 * @param <T>
 */
public final class AsyncPromiseScope<T> {

    /**
     * Callback0.
     */
    public static interface Callback0 {
        public void invoke();
    }

    /**
     * Callback.
     * @param <A>
     */
    public static interface Callback<A> {
        public void invoke(A a);
    }

    /**
     * Func0.
     * @param <R>
     */
    public static interface Func0<R> {
        public R apply();
    }

    /**
     * Func.
     * @param <A>
     * @param <R>
     */
    public static interface Func<A, R> {
        public R apply(A a);
    }

    public static class Context {

        private final Map<String, Object> properties;

        private Context() {
            properties = new HashMap<>();
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String propertyName) {
            return (T) properties.get(propertyName);
        }

        public <T> void set(String propertyName, T value) {
            properties.put(propertyName, value);
        }

        public void clear() {
            properties.clear();
        }
    }

    private static final Timer timer = new Timer();

    public static <T> AsyncPromiseScope<T> wrap(ListenableFuture<T> future) {
        return new AsyncPromiseScope<T>(future);
    }

    private final ListenableFuture<T> future;

    private AsyncPromiseScope(ListenableFuture<T> future) {
        this.future = future;
    }

    public ListenableFuture<T> wrapped() {
        return future;
    }

    public static <T> AsyncPromiseScope<T> pure(T t) {
        return wrap(Futures.immediateFuture(t));
    }

    public static <T> AsyncPromiseScope<T> throwing(Throwable throwable) {
        return wrap(Futures.<T>immediateFailedFuture(throwable));
    }

    public static <T> AsyncPromiseScope<T> delayed(long delay, TimeUnit unit, final Func0<T> func) {
        final SettableFuture<T> future = SettableFuture.create();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    future.set(func.apply());
                }
                catch (Throwable ex) {
                    future.setException(ex);
                }
            }
        }, unit.toMillis(delay));

        return wrap(future);
    }

    public static <T> AsyncPromiseScope<T> delayed(long delay, TimeUnit unit, final Closure closure) {
        return delayed(delay, unit, new Func0<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T apply() {
                return (T) closure.call();
            }
        });
    }

    // it could introduce multiple threads using the same thread local. Disable for now
    @Deprecated
    public static <T> AsyncPromiseScope<List<T>> all(Iterable<AsyncPromiseScope<? extends T>> promises) {
        List<ListenableFuture<? extends T>> futures = new ArrayList<>();

        for (AsyncPromiseScope<? extends T> promise : promises) {
            futures.add(promise.wrapped());
        }

        return wrap(Futures.allAsList(futures));
    }

    // it could introduce multiple threads using the same thread local. Disable for now
    @Deprecated
    @SafeVarargs
    public static <T> AsyncPromiseScope<List<T>> all(AsyncPromiseScope<? extends T>... promises) {
        return all(Arrays.asList(promises));
    }

    @SuppressWarnings("unchecked")
    public static <T> AsyncPromiseScope<Void> each(final Iterator<T> iterator, final Closure<AsyncPromiseScope> closure) {
        return each(iterator, new Func<T, AsyncPromiseScope>() {
            @Override
            public AsyncPromiseScope apply(T t) {
                return closure.call(t);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> AsyncPromiseScope<Void> each(final Iterator<T> iterator, final Func<? super T, AsyncPromiseScope> func) {
        AsyncPromiseScope lastPromise = AsyncPromiseScope.pure(null);
        while (iterator != null && iterator.hasNext()) {
            final T e = iterator.next();
            lastPromise = lastPromise.then(new Func<Object, AsyncPromiseScope>() {
                @Override
                public AsyncPromiseScope apply(Object obj) {
                    return func.apply(e);
                }
            });
        }
        return lastPromise.syncThen(new Func<Object, Void>() {
            @Override
            public Void apply(Object o) {
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> AsyncPromiseScope<Void> each(final Iterable<T> iterable, final Closure<AsyncPromiseScope> closure) {
        return each(iterable == null ? null : iterable.iterator(), closure);
    }

    @SuppressWarnings("unchecked")
    public static <T> AsyncPromiseScope<Void> each(final Iterable<T> iterable, final Func<? super T, AsyncPromiseScope> func) {
        return each(iterable == null ? null : iterable.iterator(), func);
    }

    public AsyncPromiseScope<T> recover(final Func<Throwable, AsyncPromiseScope<T>> func) {
        return wrap(Futures.withFallback(future, new FutureFallback<T>() {
            @Override
            public ListenableFuture<T> create(Throwable t) {
                return func.apply(t).wrapped();
            }
        }));
    }

    public AsyncPromiseScope<T> recover(final Closure closure) {
        return recover(new Func<Throwable, AsyncPromiseScope<T>>() {
            @Override
            @SuppressWarnings("unchecked")
            public AsyncPromiseScope<T> apply(Throwable throwable) {
                return (AsyncPromiseScope<T>) closure.call(throwable);
            }
        });
    }

    public AsyncPromiseScope<T> syncRecover(final Func<Throwable, T> func) {
        return recover(new Func<Throwable, AsyncPromiseScope<T>>() {
            @Override
            public AsyncPromiseScope<T> apply(Throwable throwable) {
                return AsyncPromiseScope.pure(func.apply(throwable));
            }
        });
    }

    public AsyncPromiseScope<T> syncRecover(final Closure closure) {
        return syncRecover(new Func<Throwable, T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T apply(Throwable throwable) {
                return (T) closure.call(throwable);
            }
        });
    }

    public <R> AsyncPromiseScope<R> then(final Func<? super T, AsyncPromiseScope<R>> func) {
        return wrap(Futures.transform(future, new AsyncFunction<T, R>() {
            @Override
            public ListenableFuture<R> apply(T input) {
                return func.apply(input).wrapped();
            }
        }));
    }

    public <R> AsyncPromiseScope<R> then(final Closure closure) {
        return then(new Func<T, AsyncPromiseScope<R>>() {
            @Override
            @SuppressWarnings("unchecked")
            public AsyncPromiseScope<R> apply(T t) {
                return (AsyncPromiseScope<R>) closure.call(t);
            }
        });
    }

    public <R> AsyncPromiseScope<R> syncThen(final Func<? super T, R> func) {
        return then(new Func<T, AsyncPromiseScope<R>>() {
            @Override
            public AsyncPromiseScope<R> apply(T t) {
                return AsyncPromiseScope.pure(func.apply(t));
            }
        });
    }

    public <R> AsyncPromiseScope<R> syncThen(final Closure closure) {
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
    public void onSuccess(final Callback<? super T> action) {
        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                action.invoke(result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public void onSuccess(final Closure closure) {
        onSuccess(new Callback<T>() {
            @Override
            public void invoke(T t) {
                closure.call(t);
            }
        });
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public void onFailure(final Callback<Throwable> action) {
        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
            }

            @Override
            public void onFailure(Throwable t) {
                action.invoke(t);
            }
        });
    }

    // the sequence of the invocation is not guaranteed. Disable for now.
    @Deprecated
    public void onFailure(final Closure closure) {
        onFailure(new Callback<Throwable>() {
            @Override
            public void invoke(Throwable throwable) {
                closure.call(throwable);
            }
        });
    }
}
