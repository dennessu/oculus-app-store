package com.junbo.langur.core.transaction

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.interceptor.TransactionAttribute
import org.springframework.transaction.interceptor.TransactionAttributeSource
import org.springframework.transaction.interceptor.TransactionInterceptor
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager
import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.springframework.transaction.interceptor.TransactionAspectSupport.InvocationCallback
import org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo

import java.lang.reflect.Method

/**
 * Created by kg on 3/4/14.
 */
@CompileStatic
@SuppressWarnings(['UnnecessaryOverridingMethod', 'UnusedImport', 'CatchThrowable'])
class AsyncTransactionInterceptor extends TransactionInterceptor {

    AsyncTransactionInterceptor() {
    }

    AsyncTransactionInterceptor(PlatformTransactionManager ptm, Properties attributes) {
        super(ptm, attributes)
    }

    AsyncTransactionInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource tas) {
        super(ptm, tas)
    }

    @Override
    protected Object invokeWithinTransaction(Method method, Class<?> targetClass, InvocationCallback invocation)
            throws Throwable {

        // If the transaction attribute is null, the method is non-transactional.
        TransactionAttribute txAttr = transactionAttributeSource.getTransactionAttribute(method, targetClass)
        PlatformTransactionManager tm = determineTransactionManager(txAttr)
        String joinpointIdentification = methodIdentification(method, targetClass)

        if (txAttr == null || !(tm instanceof CallbackPreferringPlatformTransactionManager)) {
            // Standard transaction demarcation with getTransaction and commit/rollback calls.
            TransactionInfo txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification)
            Object retVal
            try {
                // This is an around advice: Invoke the next interceptor in the chain.
                // This will normally result in a target object being invoked.
                retVal = invocation.proceedWithInvocation()
            }
            catch (Throwable ex) {
                // target invocation exception
                cleanupTransactionInfo(txInfo)
                completeTransactionAfterThrowing(txInfo, ex)

                throw ex
            }

            if (retVal instanceof Promise) {
                return ((Promise) retVal).recover { Throwable ex ->
                    cleanupTransactionInfo(txInfo)
                    completeTransactionAfterThrowing(txInfo, ex)

                    throw ex
                }.then { Object realRetVal ->
                    cleanupTransactionInfo(txInfo)
                    commitTransactionAfterReturning(txInfo)

                    return Promise.pure(realRetVal)
                }
            }

            cleanupTransactionInfo(txInfo)
            commitTransactionAfterReturning(txInfo)

            return retVal
        }

        throw new UnsupportedOperationException('CallbackPreferringPlatformTransactionManager not supported')
    }

    @Override
    protected void cleanupTransactionInfo(TransactionInfo txInfo) {
        super.cleanupTransactionInfo(txInfo) // bridge method for the package protected method...
    }

    @Override
    protected void commitTransactionAfterReturning(TransactionInfo txInfo) {
        super.commitTransactionAfterReturning(txInfo) // bridge method for the package protected method...
    }

    @Override
    protected void completeTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {
        super.completeTransactionAfterThrowing(txInfo, ex) // bridge method for the package protected method...
    }
}
