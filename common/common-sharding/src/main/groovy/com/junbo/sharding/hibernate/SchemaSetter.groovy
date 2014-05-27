package com.junbo.sharding.hibernate

import com.jolbox.bonecp.ConnectionHandle
import com.jolbox.bonecp.hooks.AbstractConnectionHook
import groovy.transform.CompileStatic
import org.springframework.jdbc.datasource.ConnectionProxy

import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Shenhua on 4/1/2014.
 * Set the PostgreSQL schema when connection is fetched from pool.
 */
@CompileStatic
@SuppressWarnings('JdbcConnectionReference')
class SchemaSetter extends AbstractConnectionHook {

    private final Map<Connection, String> lastSchemaMap

    SchemaSetter() {
        lastSchemaMap = new ConcurrentHashMap<>()
    }

    @Override
    void onAcquire(ConnectionHandle connection) {
        def internalConnection = connection.internalConnection
        if (connection == null) {
            throw new IllegalStateException('internalConnection is null')
        }

        def oldSchema = lastSchemaMap.put(internalConnection, 'public')

        if (oldSchema != null) {
            throw new IllegalStateException("oldSchema $oldSchema != null")
        }
    }

    @Override
    void onDestroy(ConnectionHandle connection) {
        def internalConnection = connection.internalConnection
        if (connection == null) {
            throw new IllegalStateException('internalConnection is null')
        }

        def existingSchema = lastSchemaMap.remove(internalConnection)

        if (existingSchema == null) {
            throw new IllegalStateException("existingSchema $existingSchema == null")
        }
    }

    void setSchema(Connection connection, String schema) throws SQLException {
        if (connection instanceof ConnectionProxy) {
            connection = ((ConnectionProxy) connection).targetConnection
        }

        connection = ((ConnectionHandle) connection).internalConnection
        if (connection == null) {
            throw new IllegalStateException('internalConnection is null')
        }

        def lastSchema = lastSchemaMap.get(connection)
        if (lastSchema == null) {
            throw new IllegalStateException("lastSchema for $connection is null")
        }

        if (lastSchema != schema) {
            def statement = connection.createStatement()

            try {
                statement.execute("SET SCHEMA '$schema'")
                lastSchemaMap.put(connection, schema)
            } finally {
                statement.close()
            }
        }
    }
}
