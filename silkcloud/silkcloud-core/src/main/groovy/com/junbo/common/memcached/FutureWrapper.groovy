package com.junbo.common.memcached
import com.junbo.langur.core.profiling.ProfilingHelper
import groovy.transform.CompileStatic

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
/**
 * FutureWrapper.
 */
@CompileStatic
class FutureWrapper<V> implements Future<V> {
    private String method;
    private String key;
    private Future<V> future;

    public FutureWrapper(String method, String key, Closure<Future<V>> closure) {
        this.method = method;
        this.key = key;
        ProfilingHelper.begin("MC", "%s %s", method, key);

        try {
            this.future = closure()
        } catch (Throwable ex) {
            ProfilingHelper.err(ex)
            throw ex
        }
    }

    @Override
    boolean cancel(boolean mayInterruptIfRunning) {
        return this.future.cancel(mayInterruptIfRunning)
    }

    @Override
    boolean isCancelled() {
        return this.future.isCancelled()
    }

    @Override
    boolean isDone() {
        return this.future.isDone()
    }

    @Override
    V get() throws InterruptedException, ExecutionException {
        return (V)finishProfile {
            return this.future.get()
        }
    }

    @Override
    V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (V)finishProfile {
            return this.future.get(timeout, unit)
        }
    }

    protected static <T> T finishProfile(Closure<T> closure) {
        try {
            T response = closure()
            ProfilingHelper.end("(OK) %s", resultToString(response))
            return response
        } catch (Throwable ex) {
            ProfilingHelper.err(ex)
            throw ex
        }
    }

    private static String resultToString(Object result) {
        if (result == null) {
            return "null"
        } else if (result instanceof Boolean) {
            return result.toString()
        }

        return "found"
    }
}
