package com.junbo.sharding.transaction

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.ConnectionProxy
import org.springframework.jdbc.datasource.DelegatingDataSource

import javax.sql.DataSource
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.sql.Connection
import java.sql.SQLException

/**
 * Created by kg on 5/24/2014.
 */
@CompileStatic
class SimpleDataSourceProxy extends DelegatingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataSourceProxy)

    private final String schema

    SimpleDataSourceProxy(DataSource targetDataSource, String schema) {
        super(targetDataSource)

        this.schema = schema
    }


    private Connection doGetConnection() throws SQLException {
        def connection = super.connection

        try {
            def statement = connection.createStatement()

            try {
                // has to commit. Otherwise SET SCHEMA could be rolled back by others.
                if (connection.autoCommit) {
                    statement.execute("SET SCHEMA '$schema'")
                } else {
                    statement.addBatch("SET SCHEMA '$schema'")
                    statement.addBatch('COMMIT')
                    statement.executeBatch()
                }
            } finally {
                statement.close()
            }
        } catch (SQLException ex) {
            try {
                connection.close()
            } catch (Exception closeEx) {
                LOGGER.warn("Failed to close connection $connection", closeEx)
            }

            throw ex
        }

        return connection
    }

    @Override
    Connection getConnection() throws SQLException {

        def txObject = SimpleTransactionManager.currentTransactionObject

        if (txObject == null) {
            if (LOGGER.warnEnabled) {
                LOGGER.warn("dataSource [$targetDataSource] is used without a SimpleTransactionManager")
            }

            return doGetConnection()
        }

        Connection con = txObject.getEnlistedConnection(this)

        if (con != null) {
            if (LOGGER.debugEnabled) {
                LOGGER.debug("connection for dataSource [$targetDataSource] already exists. try to reuse.");
            }

            return con;
        }

        con = doGetConnection()

        try {
            if (con.readOnly) {
                con.readOnly = false
            }

            if (con.autoCommit) {
                con.autoCommit = false
            }

            // Transaction Timeout Not Supported.

            // Transaction Isolation Not Supported.

        } catch (SQLException ex) {
            try {
                con.close()
            } catch (Exception closeEx) {
                LOGGER.warn("Failed to close connection $con", closeEx)
            }

            throw ex
        }

        con = suppressClose(con)

        txObject.enlistConnection(this, con)

        if (LOGGER.debugEnabled) {
            LOGGER.debug("connection $con is created and enlisted for dataSource [$targetDataSource].")
        }

        return con
    }

    @Override
    Connection getConnection(String username, String password) throws SQLException {
        throw new IllegalStateException('Not Supported')
    }


    private static final Class<?>[] PROXY_INTERFACES = [ConnectionProxy] as Class<?>[]

    private static Connection suppressClose(Connection connection) {
        return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                SimpleDataSourceProxy.classLoader, PROXY_INTERFACES,
                new CloseSuppressingInvocationHandler(connection))
    }

    private static class CloseSuppressingInvocationHandler implements InvocationHandler {

        private final Connection target

        CloseSuppressingInvocationHandler(Connection target) {
            this.target = target
        }

        @SuppressWarnings("rawtypes")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...

            if (method.name == 'equals') {
                // Only consider equal when proxies are identical.
                return (proxy.is(args[0]))
            } else if (method.name == 'hashCode') {
                // Use hashCode of PersistenceManager proxy.
                return System.identityHashCode(proxy)
            } else if (method.name == 'unwrap') {
                if (((Class) args[0]).isInstance(proxy)) {
                    return proxy
                }
            } else if (method.name == 'isWrapperFor') {
                if (((Class) args[0]).isInstance(proxy)) {
                    return true
                }
            } else if (method.name == 'close') {
                // Handle close method: suppress, not valid.
                return null
            } else if (method.name == 'isClosed') {
                return false
            } else if (method.name == 'getTargetConnection') {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target
            }

            // Invoke method on target Connection.
            try {
                return method.invoke(this.target, args)
            } catch (InvocationTargetException ex) {
                throw ex.targetException
            }
        }
    }
}
