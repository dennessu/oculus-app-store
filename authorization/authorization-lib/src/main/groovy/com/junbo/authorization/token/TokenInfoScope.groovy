package com.junbo.authorization.token

import com.junbo.authorization.AuthorizeContext
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
@SuppressWarnings(['CatchThrowable', 'CloseWithoutCloseable'])
class TokenInfoScope implements AutoCloseable {

    static <T> T withNull(Closure<T> closure) {
        return with(null, closure)
    }

    static <T> T with(TokenInfo tokenInfo, Closure<T> closure) {
        def scope = new TokenInfoScope(tokenInfo)

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

    static <T> T with(TokenInfo tokenInfo, Promise.Func0<T> closure) {
        def scope = new TokenInfoScope(tokenInfo)

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

    private final TokenInfo oldTokenInfo

    TokenInfoScope() {
        this(null)
    }

    TokenInfoScope(TokenInfo tokenInfo) {
        oldTokenInfo = AuthorizeContext.currentTokenInfo
        AuthorizeContext.currentTokenInfo = tokenInfo
    }

    @Override
    void close() {
        AuthorizeContext.currentTokenInfo = oldTokenInfo
    }
}
