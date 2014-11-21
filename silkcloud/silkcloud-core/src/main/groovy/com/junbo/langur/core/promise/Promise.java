// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.*;
import groovy.lang.Closure;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by kg on 2/14/14.
 *
 * @param <T>
 */
public class Promise<T> {

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

    private static final Timer timer = new Timer();

    public static <T> Promise<T> wrap(ListenableFuture<T> future) {
        return new Promise<T>(future);
    }

    public static Promise pure() {
        return pure(null);
    }

    public static <T> Promise<T> pure(T t) {
        return wrap(Futures.immediateFuture(t));
    }

    public static <T> Promise<T> throwing(Throwable throwable) {
        return wrap(Futures.<T>immediateFailedFuture(throwable));
    }

    public static <T> Promise<T> delayed(long delay, TimeUnit unit) {
        final SettableFuture<T> future = SettableFuture.create();

        final Runnable runnable = new RunnableWrapper(new Runnable() {
            @Override
            public void run() {
                future.set(null);
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, unit.toMillis(delay));

        return wrap(future);
    }

    public static <T> Promise<List<T>> all(final Iterable<?> iterable, final Closure<Promise<? extends T>> closure) {
        final Iterator<?> iterator = iterable.iterator();
        List<ListenableFuture<? extends T>> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            try (ThreadLocalRequireNew scope = new ThreadLocalRequireNew()) {
                futures.add(closure.call(item).wrapped());
            }
        }
        return wrap(Futures.allAsList(futures));
    }

    public static <T> Promise<List<T>> parallel(final Iterable<?> iterable, final Closure<Promise<? extends T>> closure) {
        final Iterator<?> iterator = iterable.iterator();
        List<ListenableFuture<? extends T>> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            futures.add(closure.call(item).wrapped());
        }
        return wrap(Futures.allAsList(futures));
    }

    public static <T> Promise<List<T>> parallel(final Closure<Promise<? extends T>>... closures) {
        Assert.notNull(closures);
        List<ListenableFuture<? extends T>> futures = new ArrayList<>();
        for (Closure<Promise<? extends T>> closure : closures) {
            futures.add(closure.call().wrapped());
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

    private Promise(ListenableFuture<T> future) {
        this.future = new ListenableFutureWrapper<T>(future);
    }

    private ListenableFuture<T> wrapped() {
        return future;
    }

    public T get() {
        Looper.tryToWait(future);

        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }

            throw new RuntimeException(cause);
        } catch (InterruptedException | TimeoutException ex) {
            throw new RuntimeException(ex);
        }
    }


    public Promise<T> recover(final Func<Throwable, Promise<T>> func) {
        return wrap(Futures.withFallback(future, new FutureFallback<T>() {
            @Override
            public ListenableFuture<T> create(Throwable t) throws Exception {
                Promise<T> result = func.apply(t);
                if (result == null) {
                    throw new RuntimeException("func " + funcToString(func) + " returns null");
                }

                return result.wrapped();
            }
        }, ExecutorContext.getExecutor()));
    }

    public <R> Promise<R> then(final Func<? super T, Promise<R>> func, final Executor executor) {
        return wrap(Futures.transform(future, new AsyncFunction<T, R>() {
            @Override
            public ListenableFuture<R> apply(T input) {
                Promise<R> result = func.apply(input);
                if (result == null) {
                    throw new RuntimeException("func " + funcToString(func) + " returns null");
                }

                return result.wrapped();
            }
        }, executor));
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
        }, ExecutorContext.getExecutor());
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
        }, ExecutorContext.getExecutor());
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
                T result = func.apply(throwable);

                if (result instanceof Promise) {
                    throw new IllegalStateException("syncRecover should return a non Promise result.");
                }
                return Promise.pure(result);
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

                R result = func.apply(t);
                if (result instanceof Promise) {
                    throw new IllegalStateException("syncThen should return a non Promise result.");
                }

                return Promise.pure(result);
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
    public final void onFailure(final Closure closure) {
        onFailure(new Callback<Throwable>() {
            @Override
            public void invoke(Throwable throwable) {
                closure.call(throwable);
            }
        });
    }


    private static String funcToString(Func func) {
        try {
            Field field = func.getClass().getDeclaredField("val$closure");

            Object closure = field.get(func);
            return closure.toString();
        } catch (Exception e) {
            return func.toString();
        }
    }
}
