package com.junbo.sharding.dualwrite

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.util.Identifiable
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.DeleteMethod
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.dualwrite.annotations.WriteMethod
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import com.junbo.sharding.dualwrite.strategies.SingleWriteStrategy
import com.junbo.sharding.dualwrite.strategies.SyncDualWriteStrategy
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.PlatformTransactionManager

import javax.transaction.TransactionManager
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by minhao on 4/20/14.
 */
@CompileStatic
class RepositoryFactoryBean<T> implements FactoryBean<T>, InitializingBean {
    private Class<T> repositoryInterface
    private BaseRepository sqlRepositoryImpl
    private BaseRepository cloudantRepositoryImpl
    private Class entityType

    private PendingActionRepository sqlPendingActionRepository
    private PendingActionRepository cloudantPendingActionRepository

    private boolean ignoreDualWriteErrors;

    private PlatformTransactionManager platformTransactionManager
    private TransactionManager transactionManager

    private DataAccessStrategy sqlOnlyStrategy;
    private DataAccessStrategy sqlFirstStrategy;
    private DataAccessStrategy cloudantOnlyStrategy;

    @Required
    public void setRepositoryInterface(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface
    }

    public void setSqlRepositoryImpl(BaseRepository sqlRepositoryImpl) {
        this.sqlRepositoryImpl = sqlRepositoryImpl
    }

    public void setCloudantRepositoryImpl(BaseRepository cloudantRepositoryImpl) {
        this.cloudantRepositoryImpl = cloudantRepositoryImpl
    }

    public void setSqlPendingActionRepository(PendingActionRepository sqlPendingActionRepository) {
        this.sqlPendingActionRepository = sqlPendingActionRepository
    }

    public void setCloudantPendingActionRepository(PendingActionRepository cloudantPendingActionRepository) {
        this.cloudantPendingActionRepository = cloudantPendingActionRepository
    }

    void setIgnoreDualWriteErrors(boolean ignoreDualWriteErrors) {
        this.ignoreDualWriteErrors = ignoreDualWriteErrors
    }

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        validate(repositoryInterface);
        this.entityType = resolveEntityType(cloudantRepositoryImpl.getClass());

        if (sqlRepositoryImpl == null && cloudantRepositoryImpl == null) {
            throw new RuntimeException("Cannot create RepositoryProxy if both SQL and Cloudant Repository implementation are null.");
        }

        if (sqlRepositoryImpl != null) {
            sqlOnlyStrategy = new SingleWriteStrategy(sqlRepositoryImpl)
        }
        if (cloudantRepositoryImpl != null) {
            cloudantOnlyStrategy = new SingleWriteStrategy(cloudantRepositoryImpl)
        }

        if (sqlRepositoryImpl != null && cloudantRepositoryImpl != null && sqlPendingActionRepository != null) {
            sqlFirstStrategy = new SyncDualWriteStrategy(
                    sqlRepositoryImpl,
                    sqlPendingActionRepository,
                    new PendingActionReplayer(
                            cloudantRepositoryImpl,
                            sqlPendingActionRepository,
                            platformTransactionManager,
                            entityType
                    ),
                    transactionManager,
                    ignoreDualWriteErrors,
            );
        }
    }

    @Override
    public T getObject() throws Exception {
        return RepositoryProxy.newProxyInstance(
                repositoryInterface,
                sqlOnlyStrategy,
                sqlFirstStrategy,
                cloudantOnlyStrategy
        );
    }

    @Override
    public Class<?> getObjectType() {
        return this.repositoryInterface
    }

    @Override
    public boolean isSingleton() {
        return true
    }

    private static void validate(Class<?> repositoryInterface) {
        // validate the interface
        Class entityType;
        Method deleteMethod;
        for (Method m in repositoryInterface.declaredMethods) {
            if (m.getAnnotation(ReadMethod) != null) {
                validateReadMethod(m)
                continue;
            }
            if (m.getAnnotation(WriteMethod) != null) {
                entityType = validateWriteMethod(m, entityType)
                continue;
            }
            if (m.getAnnotation(DeleteMethod) != null) {
                validateDeleteMethod(m, deleteMethod);
                deleteMethod = m;
                continue;
            }
            throw new RuntimeException(
                    "Unspecified Read/Write annotation on method ${m.name} of Class: ${repositoryInterface.canonicalName}");
        }
    }

    private static void validateReadMethod(Method method) {
        if (method.returnType != Promise) {
            throw new RuntimeException("Repository ReadMethod ${method} doesn't return Promise<T>");
        }
    }

    private static Class validateWriteMethod(Method method, Class entityType) {
        if (method.returnType != Promise) {
            throw new RuntimeException("Repository WriteMethod ${method} doesn't return Promise<T>");
        }
        if (method.genericReturnType instanceof ParameterizedType) {
            def returnType = (ParameterizedType)method.genericReturnType
            if (!returnType.actualTypeArguments.length == 1 || !(returnType.actualTypeArguments[0] instanceof Class)) {
                throw new RuntimeException("Repository WriteMethod ${method} return type parameter T is not a class");
            }
            def cls = (Class)returnType.actualTypeArguments[0];
            if (!CloudantEntity.isAssignableFrom(cls)) {
                throw new RuntimeException("Return type ${cls.name} does not implement CloudantEntity")
            }
            if (!Identifiable.isAssignableFrom(cls)) {
                throw new RuntimeException("Return type ${cls.name} does not implement Identifiable")
            }

            if (entityType != cls) {
                throw new RuntimeException("Repository should return same entity type in WriteMethods. Found ${cls.name} and ${entityType.name}")
            }

            return cls;
        } else {
            throw new RuntimeException("Repository WriteMethod ${method} cannot find type parameter T");
        }
    }

    private static void validateDeleteMethod(Method method, Method previousFoundDeleteMethod) {
        if (method.returnType != Promise) {
            throw new RuntimeException("Repository DeleteMethod ${method} doesn't return Promise<T>");
        }

        if (previousFoundDeleteMethod != null) {
            throw new RuntimeException("Repository only supports one DeleteMethod: Promise<Void> delete(K id). Found two: ${method} and ${previousFoundDeleteMethod}");
        }
    }

    private static Class resolveEntityType(Class repositoryType) {
        Class c = repositoryType;
        while (c != null) {
            Type t = c.getGenericSuperclass();
            if (t instanceof ParameterizedType) {
                Type[] p = ((ParameterizedType) t).getActualTypeArguments();
                for (Type t2 : p) {
                    if (t2 instanceof Class && CloudantEntity.isAssignableFrom((Class)t2)) {
                        return (Class)t2;
                    }
                }
            }
        }
        throw new RuntimeException("Cannot find entity class from repository: " + repositoryType);
    }
}
