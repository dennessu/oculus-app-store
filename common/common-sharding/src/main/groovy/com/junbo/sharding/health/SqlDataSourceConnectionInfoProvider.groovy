/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.health

import com.junbo.apphost.core.health.ConnectionInfoProvider
import com.junbo.sharding.hibernate.ShardMultiTenantConnectionProvider
import com.junbo.sharding.transaction.SimpleDataSourceProxy
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.pool.HikariPool
import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import javax.sql.DataSource
import java.lang.reflect.Field


/**
 * SqlDataSourceConnectionInfoProvider.
 */
@CompileStatic
class SqlDataSourceConnectionInfoProvider implements ConnectionInfoProvider, ApplicationContextAware {
    private static final Field HIKARI_DATA_SOURCE_POOL_FIELD;

    static {
        HIKARI_DATA_SOURCE_POOL_FIELD = HikariDataSource.getDeclaredField('pool')
        HIKARI_DATA_SOURCE_POOL_FIELD.setAccessible(true)
    }

    private ApplicationContext applicationContext

    private Map<String, ShardMultiTenantConnectionProvider> connectionProviders

    @Override
    Map getConnectionInfo() {
        Map<String, String> keyMap = [:] as Map<String, String>
        Map<String, String> result = [:] as Map<String, String>
        for (Map.Entry<String, ShardMultiTenantConnectionProvider> entry : connectionProviders.entrySet()) {
            Map<String, SimpleDataSourceProxy> dataSources = entry.value.dataSourceMap
            for (Map.Entry<String, SimpleDataSourceProxy> dataSourceEntry : dataSources.entrySet()) {
                DataSource dataSource = dataSourceEntry.value.targetDataSource

                if (dataSource instanceof HikariDataSource) {
                    HikariPool hikariPool = (HikariPool) HIKARI_DATA_SOURCE_POOL_FIELD.get(dataSource)
                    if (hikariPool != null) {
                        String status = "total/active/idle/threadAwaiting: $hikariPool.totalConnections/" +
                                "$hikariPool.activeConnections/$hikariPool.idleConnections/" +
                                "$hikariPool.threadsAwaitingConnection"
                        String key = "$entry.key:$dataSourceEntry.key"
                        keyMap[key] = hikariPool.toString()
                        result[hikariPool.toString()] = status
                    }
                }
            }
        }

        return [ "poolStatus": result, "shardMap": keyMap ] as Map
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
        this.connectionProviders = applicationContext.getBeansOfType(ShardMultiTenantConnectionProvider)
    }
}
