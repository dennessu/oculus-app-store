package com.junbo.sharding.hibernate

import bitronix.tm.resource.jdbc.ConnectionCustomizer
import bitronix.tm.resource.jdbc.PooledConnectionProxy
import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
@SuppressWarnings('JdbcConnectionReference')
class SchemaSetter implements ConnectionCustomizer {

    private final Map<Connection, String> lastSchemaMap

    SchemaSetter() {
        lastSchemaMap = new ConcurrentHashMap<>()
    }

    @Override
    void onAcquire(Connection connection, String uniqueName) {
        lastSchemaMap.put(connection, 'public')
    }

    @Override
    void onDestroy(Connection connection, String uniqueName) {
        lastSchemaMap.remove(connection)
    }

    void setSchema(Connection connection, String schema) throws SQLException {
        if (connection instanceof PooledConnectionProxy) {
            connection = ((PooledConnectionProxy) connection).proxiedDelegate
        }

        def lastSchema = lastSchemaMap.get(connection)
        if (lastSchema == null) {
            throw new IllegalStateException('lastSchema is null')
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
