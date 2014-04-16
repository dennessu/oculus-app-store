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
 * Created by kg on 2/14/14.
 * @param <T>
 */
public final class Promise<T> {

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

    public static <T> Promise<T> wrap(ListenableFuture<T> future) {
        return new Promise<T>(future);
    }

    private final ListenableFuture<T> future;

    private Promise(ListenableFuture<T> future) {
        this.future = future;
    }

    public ListenableFuture<T> wrapped() {
        return future;
    }

    public static <T> Promise<T> pure(T t) {
        return wrap(Futures.immediateFuture(t));
    }

    public static <T> Promise<T> throwing(Throwable throwable) {
        return wrap(Futures.<T>immediateFailedFuture(throwable));
    }

    public static <T> Promise<T> delayed(long delay, TimeUnit unit, final Func0<T> func) {
        final Func0<T> wrapped = Wrapper.wrap(func);

        final SettableFuture<T> future = SettableFuture.create();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    future.set(wrapped.apply());
                }
                catch (Throwable ex) {
                    future.setException(ex);
                }
            }
        }, unit.toMillis(delay));

        return wrap(future);
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

    // it could introduce multiple threads using the same thread local. Disable for now
    @Deprecated
    public static <T> Promise<List<T>> all(Iterable<Promise<? extends T>> promises) {
        List<ListenableFuture<? extends T>> futures = new ArrayList<>();

        for (Promise<? extends T> promise : promises) {
            futures.add(promise.wrapped());
        }

        return wrap(Futures.allAsList(futures));
    }

    // it could introduce multiple threads using the same thread local. Disable for now
    @Deprecated
    @SafeVarargs
    public static <T> Promise<List<T>> all(Promise<? extends T>... promises) {
        return all(Arrays.asList(promises));
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise<Void> each(final Iterator<T> iterator, final Closure<Promise> closure) {
        return each(iterator, new Func<T, Promise>() {
            @Override
            public Promise apply(T t) {
                return closure.call(t);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise<Void> each(final Iterator<T> iterator, final Func<? super T, Promise> func) {
        Promise lastPromise = Promise.pure(null);
        while (iterator != null && iterator.hasNext()) {
            final T e = iterator.next();
            lastPromise = lastPromise.then(new Func<Object, Promise>() {
                @Override
                public Promise apply(Object obj) {
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
    public static <T> Promise<Void> each(final Iterable<T> iterable, final Closure<Promise> closure) {
        return each(iterable == null ? null : iterable.iterator(), closure);
    }

    @SuppressWarnings("unchecked")
    public static <T> Promise<Void> each(final Iterable<T> iterable, final Func<? super T, Promise> func) {
        return each(iterable == null ? null : iterable.iterator(), func);
    }

    public Promise<T> recover(final Func<Throwable, Promise<T>> func) {
        final Func<Throwable, Promise<T>> wrapped = Wrapper.wrap(func);

        return wrap(Futures.withFallback(future, new FutureFallback<T>() {
            @Override
            public ListenableFuture<T> create(Throwable t) {
                return wrapped.apply(t).wrapped();
            }
        }));
    }

    public Promise<T> recover(final Closure closure) {
        return recover(new Func<Throwable, Promise<T>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Promise<T> apply(Throwable throwable) {
                return (Promise<T>) closure.call(throwable);
            }
        });
    }

    public Promise<T> syncRecover(final Func<Throwable, T> func) {
        return recover(new Func<Throwable, Promise<T>>() {
            @Override
            public Promise<T> apply(Throwable throwable) {
                return Promise.pure(func.apply(throwable));
            }
        });
    }

    public Promise<T> syncRecover(final Closure closure) {
        return syncRecover(new Func<Throwable, T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T apply(Throwable throwable) {
                return (T) closure.call(throwable);
            }
        });
    }

    public <R> Promise<R> then(final Func<? super T, Promise<R>> func) {
        final Func<? super T, Promise<R>> wrapped = Wrapper.wrap(func);

        return wrap(Futures.transform(future, new AsyncFunction<T, R>() {
            @Override
            public ListenableFuture<R> apply(T input) {
                return wrapped.apply(input).wrapped();
            }
        }));
    }

    public <R> Promise<R> then(final Closure closure) {
        return then(new Func<T, Promise<R>>() {
            @Override
            @SuppressWarnings("unchecked")
            public Promise<R> apply(T t) {
                return (Promise<R>) closure.call(t);
            }
        });
    }

    public <R> Promise<R> syncThen(final Func<? super T, R> func) {
        return then(new Func<T, Promise<R>>() {
            @Override
            public Promise<R> apply(T t) {
                return Promise.pure(func.apply(t));
            }
        });
    }

    public <R> Promise<R> syncThen(final Closure closure) {
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
        final Callback<? super T> wrapped = Wrapper.wrap(action);

        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                wrapped.invoke(result);
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
        final Callback<Throwable> wrapped = Wrapper.wrap(action);

        Futures.addCallback(future, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
            }

            @Override
            public void onFailure(Throwable t) {
                wrapped.invoke(t);
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
