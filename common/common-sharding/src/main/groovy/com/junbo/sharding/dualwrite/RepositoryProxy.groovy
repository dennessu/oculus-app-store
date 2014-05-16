package com.junbo.sharding.dualwrite
import com.junbo.common.routing.model.DataAccessPolicy
import com.junbo.common.util.Context
import com.junbo.sharding.dualwrite.annotations.DeleteMethod
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.dualwrite.annotations.WriteMethod
import groovy.transform.CompileStatic

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
/**
 * Created by minhao on 4/20/14.
 */
@CompileStatic
class RepositoryProxy implements InvocationHandler {
    private final Class<?> repositoryInterface

    private DataAccessStrategy sqlOnlyStrategy;
    private DataAccessStrategy sqlFirstStrategy;
    private DataAccessStrategy cloudantOnlyStrategy;
    private DataAccessStrategy cloudantFirstStrategy;

    static <T> T newProxyInstance(
            Class<T> repositoryInterface,
            DataAccessStrategy sqlOnlyStrategy,
            DataAccessStrategy sqlFirstStrategy,
            DataAccessStrategy cloudantOnlyStrategy,
            DataAccessStrategy cloudantFirstStrategy) {

        return (T)Proxy.newProxyInstance(
                repositoryInterface.classLoader,
                [ repositoryInterface ] as Class[],
                new RepositoryProxy(
                        repositoryInterface,
                        sqlOnlyStrategy,
                        sqlFirstStrategy,
                        cloudantOnlyStrategy,
                        cloudantFirstStrategy))
    }

    private RepositoryProxy(
            Class<?> repositoryInterface,
            DataAccessStrategy sqlOnlyStrategy,
            DataAccessStrategy sqlFirstStrategy,
            DataAccessStrategy cloudantOnlyStrategy,
            DataAccessStrategy cloudantFirstStrategy) {

        this.repositoryInterface = repositoryInterface
        this.sqlOnlyStrategy = sqlOnlyStrategy
        this.sqlFirstStrategy = sqlFirstStrategy
        this.cloudantOnlyStrategy = cloudantOnlyStrategy
        this.cloudantFirstStrategy = cloudantFirstStrategy
    }

    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        def dataAccessStrategy = getEffectiveStrategy()

        if (method.getAnnotation(ReadMethod) != null) {
            return dataAccessStrategy.invokeReadMethod(method, args);
        } else if (method.getAnnotation(WriteMethod) != null) {
            return dataAccessStrategy.invokeWriteMethod(method, args);
        } else if (method.getAnnotation(DeleteMethod) != null) {
            return dataAccessStrategy.invokeDeleteMethod(method, args);
        }

        final String methodName = method.name
        final int argsCount = method.parameterTypes.length

        if ( 'toString' == methodName && argsCount == 0 ) {
            return this.toString()
        } else if ( 'equals' == methodName && argsCount == 1 ) {
            return proxy.is(args[0])
        } else if ( 'hashCode' == methodName && argsCount == 0 ) {
            return this.hashCode()
        }

        throw new RuntimeException(
                "Unspecified Read/Write/Delete annotation on method ${method.name} of Class: ${repositoryInterface.canonicalName}");
    }

    private DataAccessStrategy getEffectiveStrategy() {
        DataAccessStrategy result;
        switch (Context.get().dataAccessPolicy) {
            case DataAccessPolicy.CLOUDANT_FIRST:
                result = cloudantFirstStrategy;
                break;
            case DataAccessPolicy.CLOUDANT_ONLY:
                result = cloudantOnlyStrategy;
                break;
            case DataAccessPolicy.SQL_FIRST:
                result = sqlFirstStrategy;
                break;
            case DataAccessPolicy.SQL_ONLY:
                result = sqlOnlyStrategy;
                break;
        }
        // TODO: hack
        result = cloudantOnlyStrategy;
        if (result == null) {
            throw new RuntimeException("Execution policy ${Context.get().dataAccessPolicy} is not supported in current Repository ${repositoryInterface.name}");
        }
        return result;
    }
}
