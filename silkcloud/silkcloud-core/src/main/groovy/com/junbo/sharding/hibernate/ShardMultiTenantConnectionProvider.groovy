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
    // tenantIdentifier will be as dataCenterId:shardId
    private final Map<String, SimpleDataSourceProxy> dataSourceMap

    Map<String, SimpleDataSourceProxy> getDataSourceMap() {
        return dataSourceMap
    }

    ShardMultiTenantConnectionProvider(
            Map<String, SimpleDataSourceProxy> dataSourceMap) {

        if (dataSourceMap == null) {
            throw new IllegalArgumentException('dataSourceMap is null')
        }

        this.dataSourceMap = dataSourceMap
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

        String[] info = tenantIdentifier.split(':')
        if (info == null || info.length != 2) {
            throw new IllegalArgumentException('tenantIdentifier should be as format dataCenterId:ShardId')
        }
        int dcId = Integer.parseInt(info[0])
        int shardId = Integer.parseInt(info[1])

        if (shardId < 0) {
            throw new IllegalArgumentException("shardId $shardId should be larger than 0")
        }
        if (dcId < 0) {
            throw new IllegalArgumentException("dataCenterId $dcId should be larger than 0")
        }
        SimpleDataSourceProxy simpleDataSourceProxy = dataSourceMap.get(dcId + ":" + shardId)
        if (simpleDataSourceProxy == null) {
            throw  new IllegalArgumentException("DataSource with shardId $shardId and dcId $dcId is not found. mapSize = " +  dataSourceMap.size().toString())
        }
        return simpleDataSourceProxy
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
