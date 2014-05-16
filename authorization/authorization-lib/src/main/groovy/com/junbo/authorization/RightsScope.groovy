package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
@SuppressWarnings(['CatchThrowable', 'CloseWithoutCloseable'])
class RightsScope implements AutoCloseable {

    static <T> T withNull(Closure<T> closure) {
        return with(null, closure)
    }

    static <T> T with(Set<String> rights, Closure<T> closure) {
        def scope = new RightsScope(rights)

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

    static <T> T with(Set<String> rights, Promise.Func0<T> closure) {
        def scope = new RightsScope(rights)

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

    private final Set<String> oldRights

    RightsScope() {
        this(null)
    }

    RightsScope(Set<String> rights) {
        oldRights = AuthorizeContext.currentRights
        AuthorizeContext.currentRights = rights
    }

    @Override
    void close() {
        AuthorizeContext.currentRights = oldRights
    }
}
