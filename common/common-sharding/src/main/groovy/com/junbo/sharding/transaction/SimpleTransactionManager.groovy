package com.junbo.sharding.transaction

import groovy.transform.CompileStatic

import javax.transaction.*

/**
 * Created by kg on 5/24/2014.
 *
 * 1. Transaction Timeout Not Supported.
 * 2. Just One Phase Commit.
 * 3. ReadOnly Not Supported.
 * 4. Isolation Level Not Supported.
 */
@CompileStatic
class SimpleTransactionManager implements TransactionManager {

    private static final ThreadLocal<SimpleTransactionObject> CURRENT_TRANSACTION = new ThreadLocal<>();

    static SimpleTransactionObject getCurrentTransactionObject() {
        return CURRENT_TRANSACTION.get()
    }

    static void setCurrentTransactionObject(SimpleTransactionObject transactionObject) {
        CURRENT_TRANSACTION.set(transactionObject)
    }

    @Override
    void begin() throws NotSupportedException, SystemException {

        SimpleTransactionObject currentTx = currentTransactionObject
        if (currentTx != null) {
            throw new NotSupportedException('nested transactions not supported')
        }

        currentTx = new SimpleTransactionObject()
        currentTransactionObject = currentTx

        currentTx.registerSynchronization(new Synchronization() {
            @Override
            void beforeCompletion() {
            }

            @Override
            void afterCompletion(int status) {
                def existingTx = currentTransactionObject
                if (existingTx != currentTx) {
                    throw new IllegalStateException('existingTx != currentTx')
                }

                existingTx.cleanup()
                currentTransactionObject = null
            }
        })
    }

    @Override
    void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {

        SimpleTransactionObject currentTx = currentTransactionObject
        if (currentTx == null) {
            throw new IllegalStateException('no transaction started on this thread')
        }

        currentTx.commit()
    }

    @Override
    void rollback() throws IllegalStateException, SecurityException, SystemException {

        SimpleTransactionObject currentTx = currentTransactionObject
        if (currentTx == null) {
            throw new IllegalStateException('no transaction started on this thread')
        }

        currentTx.rollback()
    }

    @Override
    void setRollbackOnly() throws IllegalStateException, SystemException {

        SimpleTransactionObject currentTx = currentTransactionObject
        if (currentTx == null) {
            throw new IllegalStateException('no transaction started on this thread')
        }

        currentTx.setRollbackOnly()
    }

    @Override
    int getStatus() throws SystemException {

        SimpleTransactionObject currentTx = currentTransactionObject
        if (currentTx == null) {
            return Status.STATUS_NO_TRANSACTION
        }

        return currentTx.getStatus()
    }

    @Override
    Transaction getTransaction() throws SystemException {
        return currentTransactionObject
    }

    @Override
    void setTransactionTimeout(int seconds) throws SystemException {
        // ignored
    }

    @Override
    Transaction suspend() throws SystemException {
        SimpleTransactionObject currentTx = currentTransactionObject
        if (currentTx == null) {
            return null
        }

        currentTransactionObject = null
        return currentTx
    }

    @Override
    void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        if (transaction == null) {
            throw new InvalidTransactionException('resumed transaction cannot be null')
        }

        if (!(transaction instanceof SimpleTransactionObject)) {
            throw new InvalidTransactionException('resumed transaction must be an instance of SimpleTransactionObject')
        }

        SimpleTransactionObject tx = (SimpleTransactionObject) transaction

        if (currentTransactionObject != null) {
            throw new IllegalStateException('a transaction is already running on this thread')
        }

        currentTransactionObject = tx
    }
}
