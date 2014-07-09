package com.junbo.sharding.dualwrite
import com.junbo.common.routing.DataAccessPolicies
import com.junbo.common.routing.model.DataAccessAction
import com.junbo.common.routing.model.DataAccessPolicy
import com.junbo.common.util.Context
import com.junbo.sharding.dualwrite.annotations.DeleteMethod
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.dualwrite.annotations.WriteMethod
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by minhao on 4/20/14.
 */
@CompileStatic
class RepositoryProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryProxy.class);

    private final Class<?> repositoryInterface

    private DataAccessStrategy sqlOnlyStrategy;
    private DataAccessStrategy sqlFirstStrategy;
    private DataAccessStrategy cloudantOnlyStrategy;

    static <T> T newProxyInstance(
            Class<T> repositoryInterface,
            DataAccessStrategy sqlOnlyStrategy,
            DataAccessStrategy sqlFirstStrategy,
            DataAccessStrategy cloudantOnlyStrategy) {

        return (T)Proxy.newProxyInstance(
                repositoryInterface.classLoader,
                [ repositoryInterface ] as Class[],
                new RepositoryProxy(
                        repositoryInterface,
                        sqlOnlyStrategy,
                        sqlFirstStrategy,
                        cloudantOnlyStrategy))
    }

    private RepositoryProxy(
            Class<?> repositoryInterface,
            DataAccessStrategy sqlOnlyStrategy,
            DataAccessStrategy sqlFirstStrategy,
            DataAccessStrategy cloudantOnlyStrategy) {

        this.repositoryInterface = repositoryInterface
        this.sqlOnlyStrategy = sqlOnlyStrategy
        this.sqlFirstStrategy = sqlFirstStrategy
        this.cloudantOnlyStrategy = cloudantOnlyStrategy
    }

    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (method.getAnnotation(ReadMethod) != null) {
                def dataAccessStrategy = getEffectiveStrategy(DataAccessAction.READ, repositoryInterface)
                return dataAccessStrategy.invokeReadMethod(method, args);
            } else if (method.getAnnotation(WriteMethod) != null) {
                def dataAccessStrategy = getEffectiveStrategy(DataAccessAction.WRITE, repositoryInterface)
                return dataAccessStrategy.invokeWriteMethod(method, args);
            } else if (method.getAnnotation(DeleteMethod) != null) {
                def dataAccessStrategy = getEffectiveStrategy(DataAccessAction.WRITE, repositoryInterface)
                return dataAccessStrategy.invokeDeleteMethod(method, args);
            }
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException()
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

    private DataAccessStrategy getEffectiveStrategy(DataAccessAction action, Class<?> repositoryInterface) {

        DataAccessPolicy policy = Context.get().dataAccessPolicy;
        if (policy == null) {
            // fallback.
            policy = DataAccessPolicies.instance().getDataAccessPolicy(action, repositoryInterface);

            if (policy == null) {
                throw new RuntimeException("Cannot find effective dataAccessPolicy after fallback! Action: $action, Repo: ${repositoryInterface.name}");
            }
        }

        DataAccessStrategy result;
        switch (policy) {
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
        if (result == null) {
            throw new RuntimeException("Execution policy ${Context.get().dataAccessPolicy} is not supported in current Repository ${repositoryInterface.name}");
        }
        return result;
    }
}
