package com.junbo.sharding.transaction

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.ConnectionProxy
import org.springframework.util.Assert

import javax.transaction.*
import javax.transaction.xa.XAResource
import java.sql.Connection

/**
 * Created by kg on 5/24/2014.
 */
@CompileStatic
class SimpleTransactionObject implements Transaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTransactionObject)

    private final Map<SimpleDataSourceProxy, Connection> enlistedDataSources = new LinkedHashMap<>()

    private final List<Synchronization> synchronizations = new ArrayList<>()

    private int status = Status.STATUS_ACTIVE

    SimpleTransactionObject() {
    }

    void enlistConnection(SimpleDataSourceProxy dataSource, Connection connection) {
        Assert.notNull(dataSource, 'dataSource')
        Assert.notNull(connection, 'connection')

        if (status == Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("transaction hasn't started yet")
        }

        if (status == Status.STATUS_MARKED_ROLLBACK) {
            throw new RollbackException('transaction has been marked as rollback only')
        }

        if (isDone()) {
            throw new IllegalStateException('transaction is done, cannot enlist any more resource')
        }

        Connection oldConnection = enlistedDataSources.put(dataSource, connection)

        if (oldConnection != null) {
            throw new IllegalStateException("dataSource $dataSource is already enlisted")
        }
    }

    Connection getEnlistedConnection(SimpleDataSourceProxy dataSource) {
        Assert.notNull(dataSource, 'dataSource')

        if (status == Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("transaction hasn't started yet")
        }

        if (status == Status.STATUS_MARKED_ROLLBACK) {
            throw new RollbackException('transaction has been marked as rollback only')
        }

        if (isDone()) {
            throw new IllegalStateException('transaction is done, cannot get enlisted resource')
        }

        return enlistedDataSources.get(dataSource)
    }

    @PackageScope
    void cleanup() {
        for (Connection con : enlistedDataSources.values()) {
            if (con instanceof ConnectionProxy) {
                con = ((ConnectionProxy) con).targetConnection
            }

            try {
                con.close()
            } catch (Exception ex) {
                LOGGER.warn("Failed to close connection $con", ex)
            }
        }
    }

    @Override
    void setRollbackOnly() throws IllegalStateException, SystemException {
        LOGGER.info("[ + $this + ]: set rollbackOnly = true")

        if (status == Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("transaction hasn't started yet")
        }

        if (isDone()) {
            throw new IllegalStateException('transaction is done, cannot change its status')
        }

        status = Status.STATUS_MARKED_ROLLBACK
    }

    @Override
    int getStatus() throws SystemException {
        return status
    }

    @Override
    void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, SystemException {

        if (status == Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("transaction hasn't started yet")
        }

        if (isDone()) {
            throw new IllegalStateException('transaction is done, cannot commit it')
        }

        try {
            fireBeforeCompletionEvent()
        } catch (SystemException | RuntimeException ex) {
            rollback()

            def rollbackEx = new RollbackException('Exception thrown during beforeCompletion cycle caused transaction rollback')
            rollbackEx.initCause(ex)
            throw rollbackEx
        }

        if (status == Status.STATUS_MARKED_ROLLBACK) {
            rollback()
            throw new RollbackException('transaction was marked as rollback only and has been rolled back')
        }

        status = Status.STATUS_COMMITTING

        boolean commit = true
        Exception commitException = null
        Connection firstCon = null
        Connection commitCon = null

        for (Connection con : enlistedDataSources.values()) {
            if (firstCon == null) {
                firstCon = con
            }

            if (commit) {
                try {
                    commitCon = con
                    con.commit()
                } catch (Exception ex) {
                    commitException = ex
                    commit = false
                }
            } else {
                try {
                    con.rollback()
                } catch (Exception ex) {
                    LOGGER.warn("[$this]: Rollback exception (after commit) ($con)", ex)
                }
            }
        }

        if (commitException != null) {
            Exception ex

            if (firstCon == commitCon) {
                ex = new RollbackException("Commit exception, originated at ($commitCon)")
                ex.initCause(commitException)

                status = Status.STATUS_ROLLEDBACK
            } else {
                ex = new HeuristicMixedException("Some connections committed. Others rolled back.")
                ex.initCause(commitException)

                status = Status.STATUS_UNKNOWN
            }

            fireAfterCompletionEvent()
            throw ex
        }

        status = Status.STATUS_COMMITTED
        fireAfterCompletionEvent()
    }

    void rollback() throws IllegalStateException, SystemException {

        if (status == Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("transaction hasn't started yet")
        }

        if (isDone()) {
            throw new IllegalStateException('transaction is done, cannot roll it back')
        }

        status = Status.STATUS_ROLLING_BACK

        Exception rollbackException = null
        Connection rollbackCon = null

        for (Connection con : enlistedDataSources.values()) {
            try {
                con.rollback()
            } catch (Exception ex) {
                if (rollbackException == null) {
                    rollbackException = ex
                    rollbackCon = con
                } else {
                    LOGGER.warn("[$this]: Rollback exception ($con)", ex)
                }
            }
        }

        if (rollbackException != null) {
            Exception ex = new SystemException("Rollback exception, originated at ($rollbackCon)")
            ex.initCause(rollbackException)

            status = Status.STATUS_UNKNOWN
            fireAfterCompletionEvent()

            throw ex
        }

        status = Status.STATUS_ROLLEDBACK
        fireAfterCompletionEvent()
    }

    @Override
    boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        throw new IllegalStateException('Not Supported')
    }

    @Override
    boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        throw new IllegalStateException('Not Supported')
    }

    @Override
    void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        if (status == Status.STATUS_NO_TRANSACTION) {
            throw new IllegalStateException("transaction hasn't started yet")
        }

        if (status == Status.STATUS_MARKED_ROLLBACK) {
            throw new RollbackException('transaction has been marked as rollback only')
        }

        if (isDone()) {
            throw new IllegalStateException('transaction is done, cannot register any more synchronization')
        }

        synchronizations.add(sync)
    }


    private void fireBeforeCompletionEvent() throws SystemException {
        for (Synchronization synchronization : synchronizations.reverse()) {
            try {
                synchronization.beforeCompletion()
            } catch (RuntimeException ex) {
                status = Status.STATUS_MARKED_ROLLBACK
                throw ex
            }
        }
    }

    private void fireAfterCompletionEvent() {
        for (Synchronization synchronization : synchronizations) {
            try {
                synchronization.afterCompletion(status)
            } catch (Exception ex) {
                LOGGER.warn("Synchronization.afterCompletion() call failed for $synchronization", ex)
            }
        }
    }

    private boolean isDone() {
        switch (status) {
            case Status.STATUS_PREPARING:
            case Status.STATUS_PREPARED:
            case Status.STATUS_COMMITTING:
            case Status.STATUS_COMMITTED:
            case Status.STATUS_ROLLING_BACK:
            case Status.STATUS_ROLLEDBACK:
                return true
        }

        return false
    }
}
