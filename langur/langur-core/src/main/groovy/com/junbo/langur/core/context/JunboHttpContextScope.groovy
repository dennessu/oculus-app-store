package com.junbo.langur.core.context

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by kg on 5/23/2014.
 */
@CompileStatic
@SuppressWarnings(['CatchThrowable', 'CloseWithoutCloseable'])
class JunboHttpContextScope implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JunboHttpContextScope)

    static <T> T withNull(Closure<T> closure) {
        return with(null, closure)
    }

    static <T> T with(JunboHttpContext.JunboHttpContextData data, Closure<T> closure) {
        return with(data, null, closure)
    }

    static <T> T with(JunboHttpContext.JunboHttpContextData data,
                      List<JunboHttpContextScopeListener> listeners, Closure<T> closure) {
        def scope = new JunboHttpContextScope(data, listeners)

        Object result

        try {
            result = closure()
        } catch (Throwable ex) {
            scope.close()
            throw ex
        }

        if (result instanceof Promise) {
            def promise = (Promise) result
            return (T) promise.recover { Throwable ex ->
                scope.close()
                throw ex
            }.then { Object realResult ->
                scope.close()
                return Promise.pure(realResult)
            }
        }

        scope.close()
        return result
    }

    static <T> Promise<T> with(JunboHttpContext.JunboHttpContextData data, Promise.Func0<Promise<T>> closure) {
        return with(data, null, closure)
    }

    static <T> Promise<T> with(JunboHttpContext.JunboHttpContextData data,
                      List<JunboHttpContextScopeListener> listeners, Promise.Func0<Promise<T>> closure) {
        def scope = new JunboHttpContextScope(data, listeners)

        Object result

        try {
            result = closure.apply()
        } catch (Throwable ex) {
            scope.close()
            return Promise.throwing(ex)
        }

        if (result instanceof Promise) {
            def promise = (Promise) result
            return (Promise<T>) promise.recover { Throwable ex ->
                scope.close()
                throw ex
            }.then { Object realResult ->
                scope.close()
                return Promise.pure(realResult)
            }
        }

        scope.close()
        return result
    }

    private final JunboHttpContext.JunboHttpContextData oldData
    private final List<JunboHttpContextScopeListener> listeners

    JunboHttpContextScope() {
        this(null)
    }

    JunboHttpContextScope(JunboHttpContext.JunboHttpContextData data) {
        this(data, null)
    }

    JunboHttpContextScope(JunboHttpContext.JunboHttpContextData data, List<JunboHttpContextScopeListener> listeners) {
        oldData = JunboHttpContext.data
        JunboHttpContext.data = data

        this.listeners = []

        if (listeners != null) {
            for (def listener : listeners) {
                listener.begin()

                this.listeners.add(listener)
            }
        }
    }

    @Override
    void close() {

        for (def listener : listeners.reverse()) {
            try {
                listener.end()
            } catch (Exception ex) {
                LOGGER.warn("Failed to call end() on listener: $listener", ex)
            }
        }

        JunboHttpContext.data = oldData
    }
}
