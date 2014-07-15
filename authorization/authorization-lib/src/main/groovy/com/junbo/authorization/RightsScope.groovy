package com.junbo.authorization

import com.junbo.common.model.AdminInfo
import com.junbo.common.model.ResourceMetaBase
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
@SuppressWarnings(['CatchThrowable', 'CloseWithoutCloseable'])
class RightsScope implements AutoCloseable {
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
                processRealResult(realResult, scope)
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
                processRealResult(realResult, scope)
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

    static void processRealResult(Object realResult, RightsScope scope) {
        if (realResult == null) {
            // do nothing here
        } else if (Results.isAssignableFrom(realResult.class)) {
            // In this case, it would be Results(ResourceMeta)
            Results<Object> results = realResult as Results<Object>
            results.items.each { Object obj ->
                if (ResourceMetaBase.isAssignableFrom(obj.class)) {
                    obj = (Object)filterForAdminInfo(obj as ResourceMetaBase)
                } else {
                    scope.close()
                    throw new IllegalStateException('RightScope can only return Promise<T extend ResourceMeta>,' +
                            'Promise<Results<T extends ResourceMeta> and null')
                }
            }
        } else if (ResourceMetaBase.isAssignableFrom(realResult.class)) {
            realResult = (Object)filterForAdminInfo(realResult as ResourceMetaBase)
        } else {
            // Throw exception here to identity we don't support
            scope.close()
            throw new IllegalStateException('RightScope can only return Promise<T extend ResourceMeta>,' +
                    'Promise<Results<T extends ResourceMeta> and null')
        }
    }

    static <T extends ResourceMetaBase> T filterForAdminInfo(T resourceMeta) {
        if (resourceMeta == null) {
            return resourceMeta
        }

        if (AuthorizeContext.hasRights('read.admin')) {
            AdminInfo adminInfo = new AdminInfo(
                    createdByClient: resourceMeta.createdByClient,
                    createdBy: resourceMeta.createdBy,
                    updatedByClient: resourceMeta.updatedByClient,
                    updatedBy: resourceMeta.updatedBy
            )
            resourceMeta.setAdminInfo(adminInfo)
        } else {
            resourceMeta.setAdminInfo(null)
        }
        return resourceMeta
    }
}
