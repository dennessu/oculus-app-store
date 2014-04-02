package com.junbo.sharding.hibernate

import groovy.transform.CompileStatic
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.hibernate.service.UnknownUnwrapTypeException

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException

/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
@SuppressWarnings('JdbcConnectionReference')
class ShardMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

    private final List<DataSource> dataSourceList

    private final List<String> schemaList

    ShardMultiTenantConnectionProvider(List<DataSource> dataSourceList, List<String> schemaList) {
        if (dataSourceList == null) {
            throw new IllegalArgumentException('dataSourceList is null')
        }
        if (schemaList == null) {
            throw new IllegalArgumentException('schemaList is null')
        }
        if (schemaList.size() != dataSourceList.size()) {
            throw new IllegalArgumentException('schemaList.size != dataSourceList.size')
        }

        this.dataSourceList = dataSourceList
        this.schemaList = schemaList
    }

    @Override
    Connection getAnyConnection() throws SQLException {
        throw new IllegalStateException('anyConnection not supported')
    }

    @Override
    void releaseAnyConnection(Connection connection) throws SQLException {
        throw new IllegalStateException('anyConnection not supported')
    }

    @Override
    Connection getConnection(String tenantIdentifier) throws SQLException {
        if (tenantIdentifier == null) {
            throw new IllegalArgumentException('tenantIdentifier is null')
        }

        int shardId = Integer.parseInt(tenantIdentifier)

        if (shardId < 0 || shardId >= schemaList.size()) {
            throw new IllegalArgumentException("shardId $shardId should be in [0, ${schemaList.size()})")
        }

        def dataSource = dataSourceList.get(shardId)
        def schema = schemaList.get(shardId)

        def connection = dataSource.connection
        connection.createStatement().execute("SET SCHEMA '$schema'")
        return connection
    }

    @Override
    void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.createStatement().execute("SET SCHEMA 'public'")
        connection.close()
    }

    @Override
    boolean supportsAggressiveRelease() {
        return true
    }

    @Override
    boolean isUnwrappableAs(Class unwrapType) {
        return MultiTenantConnectionProvider == unwrapType ||
                ShardMultiTenantConnectionProvider.isAssignableFrom(unwrapType)
    }

    @Override
    <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return (T) this
        }
        throw new UnknownUnwrapTypeException(unwrapType)
    }
}
