package com.junbo.langur.core.context

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by kg on 5/23/2014.
 */
@CompileStatic
@SuppressWarnings(['CatchThrowable', 'CloseWithoutCloseable'])
class JunboHttpContextScope implements AutoCloseable {

    static <T> T withNull(Closure<T> closure) {
        return with(null, closure)
    }

    static <T> T with(JunboHttpContext.JunboHttpContextData data, Closure<T> closure) {
        def scope = new JunboHttpContextScope(data)

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

    static <T> T with(JunboHttpContext.JunboHttpContextData data, Promise.Func0<T> closure) {
        def scope = new JunboHttpContextScope(data)

        Object result

        try {
            result = closure.apply()
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

    private final JunboHttpContext.JunboHttpContextData oldData

    JunboHttpContextScope() {
        this(null)
    }

    JunboHttpContextScope(JunboHttpContext.JunboHttpContextData data) {
        oldData = JunboHttpContext.data
        JunboHttpContext.data = data
    }

    @Override
    void close() {
        JunboHttpContext.data = oldData
    }
}
