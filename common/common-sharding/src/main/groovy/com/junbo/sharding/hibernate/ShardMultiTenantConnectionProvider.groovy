package com.junbo.sharding.hibernate

import com.junbo.sharding.transaction.SimpleDataSourceProxy
import groovy.transform.CompileStatic
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.hibernate.service.UnknownUnwrapTypeException

import java.sql.Connection
import java.sql.SQLException

/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
@SuppressWarnings('JdbcConnectionReference')
class ShardMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

    private final List<SimpleDataSourceProxy> dataSourceList

    ShardMultiTenantConnectionProvider(
            List<SimpleDataSourceProxy> dataSourceList) {

        if (dataSourceList == null) {
            throw new IllegalArgumentException('dataSourceList is null')
        }

        this.dataSourceList = dataSourceList
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
        return getDataSource(tenantIdentifier).connection
    }

    SimpleDataSourceProxy getDataSource(String tenantIdentifier) {
        if (tenantIdentifier == null) {
            throw new IllegalArgumentException('tenantIdentifier is null')
        }

        int shardId = Integer.parseInt(tenantIdentifier)

        if (shardId < 0 || shardId >= dataSourceList.size()) {
            throw new IllegalArgumentException("shardId $shardId should be in [0, ${dataSourceList.size()})")
        }

        return dataSourceList.get(shardId)
    }

    @Override
    void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
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
