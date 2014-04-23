package com.junbo.sharding.core

import com.junbo.common.util.Utils
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import com.junbo.sharding.core.annotations.WriteMethod
import groovy.transform.CompileStatic

import java.lang.annotation.Annotation
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by minhao on 4/20/14.
 */
@CompileStatic
class RepositoryProxy implements InvocationHandler {
    private final PersistentMode persistentMode
    private final Class<?> interfaceClass
    private final Object sqlRepositoryTarget
    private final Object cloudantRepositoryTarget
    private final Object readRepository
    private final Object primaryWriteRepository
    private final Object secondaryWriteRepository

    static <T> T newProxyInstance(Class<T> interfaceClass,
                                         Object sqlRepositoryTarget, Object cloudantRepositoryTarget,
                                         PersistentMode persistentMode) {
        return (T)Proxy.newProxyInstance(interfaceClass.classLoader, [interfaceClass].toArray(new Class[0]),
                new RepositoryProxy(sqlRepositoryTarget, cloudantRepositoryTarget,
                        persistentMode, interfaceClass))
    }

    private RepositoryProxy(Object sqlRepositoryTarget, Object cloudantRepositoryTarget,
                                PersistentMode persistentMode, Class interfaceClass) {
        this.sqlRepositoryTarget = sqlRepositoryTarget
        this.cloudantRepositoryTarget = cloudantRepositoryTarget
        this.persistentMode = persistentMode
        this.interfaceClass = interfaceClass

        if (persistentMode == PersistentMode.SQL_READ_WRITE) {
            if (this.sqlRepositoryTarget == null) {
                throw new RuntimeException('sql repository not set while persistent mode is SQL_READ_WRITE')
            }

            this.readRepository = this.sqlRepositoryTarget
            this.primaryWriteRepository = this.sqlRepositoryTarget
            this.secondaryWriteRepository = null
        }
        else if (persistentMode == PersistentMode.CLOUDANT_READ_WRITE) {
            if (this.cloudantRepositoryTarget == null) {
                throw new RuntimeException('cloudant repository not set while persistent mode is CLOUDANT_READ_WRITE')
            }

            this.readRepository = this.cloudantRepositoryTarget
            this.primaryWriteRepository = this.cloudantRepositoryTarget
            this.secondaryWriteRepository = null
        }
        else if (persistentMode == PersistentMode.CLOUDANT_READ_DUAL_WRITE_CLOUDANT_PRIMARY) {
            if (this.cloudantRepositoryTarget == null || this.sqlRepositoryTarget == null) {
                throw new RuntimeException(
                        'Both cloudant repository and sql repository need to be set in dual write mode')
            }

            this.readRepository = this.cloudantRepositoryTarget
            this.primaryWriteRepository = this.cloudantRepositoryTarget
            this.secondaryWriteRepository = this.sqlRepositoryTarget
        }
        else if (persistentMode == PersistentMode.CLOUDANT_READ_DUAL_WRITE_SQL_PRIMARY) {
            if (this.cloudantRepositoryTarget == null || this.sqlRepositoryTarget == null) {
                throw new RuntimeException(
                        'Both cloudant repository and sql repository need to be set in dual write mode')
            }

            this.readRepository = this.cloudantRepositoryTarget
            this.primaryWriteRepository = this.sqlRepositoryTarget
            this.secondaryWriteRepository = this.cloudantRepositoryTarget
        }
    }

    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Annotation[] annotations = method.declaredAnnotations

        for (Annotation annotation : annotations) {
            if (annotation instanceof ReadMethod) {
                return method.invoke(this.readRepository, args)
            }
            else if (annotation instanceof WriteMethod) {
                return ((Promise<?>) method.invoke(this.primaryWriteRepository, args)).then { Object result ->
                    if (this.secondaryWriteRepository != null) {
                        if (args.length != 1) {
                            throw new RuntimeException('WriteMethod should have only one parameter, ' +
                                    'please check write methods of Class:' + interfaceClass.canonicalName)
                        }
                        def idGetter = Utils.tryObtainGetterMethod(args[0].class, 'id')
                        def idSetter = Utils.tryObtainSetterMethod(args[0].class, 'id')
                        def resultIdGetter = Utils.tryObtainGetterMethod(result.class, 'id')

                        if (idGetter == null || idSetter == null || resultIdGetter == null) {
                            throw new RuntimeException('WriteMethod parameter should have id property' +
                                    'please check write methods of Class:' + interfaceClass.canonicalName)
                        }
                        if (idGetter.invoke(args[0]) == null) {
                            // set id
                            idSetter.invoke(args[0], resultIdGetter.invoke(result))
                        }

                        return ((Promise<?>) method.invoke(this.secondaryWriteRepository, args)).then {
                            return Promise.pure(result)
                        }
                    }

                    return Promise.pure(result)
                }
            }
        }

        final String METHOD_NAME = method.name
        final int ARG_COUNT = method.parameterTypes.length

        if ( 'toString' == METHOD_NAME && ARG_COUNT == 0 ) {
            return this.toString()
        }
        if ( 'equals' == METHOD_NAME && ARG_COUNT == 1 ) {
            return proxy.is(args[0])
        }
        if ( 'hashCode' == METHOD_NAME && ARG_COUNT == 0 ) {
            return this.hashCode()
        }

        throw new RuntimeException('Unspecified Read/Write annotation on methods of Class:'
                + interfaceClass.canonicalName)
    }
}
