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
class SchemaSetter implements ConnectionCustomizer {

    private final Map<Connection, String> lastSchema

    SchemaSetter() {
        lastSchema = new ConcurrentHashMap<>()
    }

    @Override
    void onAcquire(Connection connection, String uniqueName) {
        lastSchema.put(connection, 'public')
    }

    @Override
    void onDestroy(Connection connection, String uniqueName) {
        lastSchema.remove(connection)
    }

    void setSchema(Connection connection, String schema) throws SQLException {
        if (connection instanceof PooledConnectionProxy) {
            connection = ((PooledConnectionProxy) connection).proxiedDelegate
        }

        if (lastSchema.get(connection) != schema) {
            def statement = connection.createStatement()

            try {
                statement.execute("SET SCHEMA '$schema'")
                lastSchema.put(connection, schema)
            } finally {
                statement.close()
            }
        }
    }
}
