package com.junbo.langur.core.transaction

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.transaction.*
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

import java.lang.reflect.UndeclaredThrowableException

/**
 * Created by kg on 3/4/14.
 */
@CompileStatic
@SuppressWarnings(['CatchThrowable', 'CatchRuntimeException', 'CatchError'])
class AsyncTransactionTemplate extends TransactionTemplate {

    AsyncTransactionTemplate() {
    }

    AsyncTransactionTemplate(PlatformTransactionManager transactionManager) {
        super(transactionManager)
    }

    AsyncTransactionTemplate(PlatformTransactionManager transactionManager,
                             TransactionDefinition transactionDefinition) {
        super(transactionManager, transactionDefinition)
    }

    @Override
    <T> T execute(TransactionCallback<T> action) throws TransactionException {
        if (transactionManager instanceof CallbackPreferringPlatformTransactionManager) {
            throw new UnsupportedOperationException('CallbackPreferringPlatformTransactionManager not supported')
        } else {
            TransactionStatus status = transactionManager.getTransaction(this)
            T result
            try {
                result = action.doInTransaction(status)
            } catch (Throwable ex) {
                rollbackOnException(status, ex)

                if (ex instanceof RuntimeException || ex instanceof Error) {
                    throw ex
                }

                throw new UndeclaredThrowableException(ex, 'TransactionCallback threw undeclared checked exception')
            }

            if (result instanceof Promise) {
                return (T) ((Promise) result).recover { Throwable ex ->
                    rollbackOnException(status, ex)

                    if (ex instanceof RuntimeException || ex instanceof Error) {
                        throw ex
                    }

                    throw new UndeclaredThrowableException(ex, 'TransactionCallback threw undeclared checked exception')
                }.then { Object realResult ->
                    transactionManager.commit(status)
                    return Promise.pure(realResult)
                }
            }

            transactionManager.commit(status)
            return result
        }
    }

    private void rollbackOnException(TransactionStatus status, Throwable ex) throws TransactionException {
        logger.debug('Initiating transaction rollback on application exception', ex)
        try {
            transactionManager.rollback(status)
        } catch (TransactionSystemException ex2) {
            logger.error('Application exception overridden by rollback exception', ex)
            ex2.initApplicationException(ex)
            throw ex2
        } catch (RuntimeException ex2) {
            logger.error('Application exception overridden by rollback exception', ex)
            throw ex2
        } catch (Error err) {
            logger.error('Application exception overridden by rollback error', ex)
            throw err
        }
    }

}
