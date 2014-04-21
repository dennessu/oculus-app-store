package com.junbo.sharding.core

import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.core.annotations.ReadMethod
import com.junbo.sharding.core.annotations.WriteMethod
import groovy.transform.CompileStatic
import junit.framework.Assert

import java.lang.annotation.Annotation
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by minhao on 4/20/14.
 */
@CompileStatic
class RepositoryProxy implements InvocationHandler {
    private final ShardAlgorithm shardAlgorithm
    private final IdGenerator idGenerator
    private final PersistentMode persistentMode
    private final Class<?> interfaceClass
    private final Object sqlRepositoryTarget
    private final Object cloudantRepositoryTarget
    private final Object readRepository
    private final Object primaryWriteRepository
    private final Object secondaryWriteRepository

    static <T> T newProxyInstance(Class<T> interfaceClass,
                                         Object sqlRepositoryTarget, Object cloudantRepositoryTarget,
                                         PersistentMode persistentMode, ShardAlgorithm shardAlgorithm,
                                         IdGenerator idGenerator) {
        return (T)Proxy.newProxyInstance(interfaceClass.classLoader, [interfaceClass].toArray(new Class[0]),
                new RepositoryProxy(sqlRepositoryTarget, cloudantRepositoryTarget,
                        persistentMode, shardAlgorithm, idGenerator, interfaceClass))
    }

    private RepositoryProxy(Object sqlRepositoryTarget, Object cloudantRepositoryTarget,
                                PersistentMode persistentMode, ShardAlgorithm shardAlgorithm,
                                IdGenerator idGenerator, Class interfaceClass) {
        this.sqlRepositoryTarget = sqlRepositoryTarget
        this.cloudantRepositoryTarget = cloudantRepositoryTarget
        this.persistentMode = persistentMode
        this.shardAlgorithm = shardAlgorithm
        this.idGenerator = idGenerator
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
        Assert.assertNotNull(annotations)

        for (Annotation annotation : annotations) {
            if (annotation instanceof ReadMethod) {
                return method.invoke(this.readRepository, args)
            }
            else if (annotation instanceof WriteMethod) {
                return ((Promise<Void>)method.invoke(this.primaryWriteRepository, args)).then {
                    if (this.secondaryWriteRepository != null) {
                        return ((Promise<Void>)method.invoke(this.secondaryWriteRepository, args)).then {
                            return Promise.pure(null)
                        }
                    }
                }
            }
        }

        throw new RuntimeException('Unspecified Read/Write annotation on methods of Class:'
                + interfaceClass.canonicalName)
    }
}
