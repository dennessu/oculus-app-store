/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.pool;

import com.junbo.sharding.hibernate.ShardMultiTenantConnectionProviderFactoryBean;
import com.zaxxer.hikari.HikariDataSource;

/**
 * JunboShardMultiTenantConnectionProviderFactoryBean.
 */
public class JunboShardMultiTenantConnectionProviderFactoryBean extends ShardMultiTenantConnectionProviderFactoryBean {
    protected HikariDataSource createDataSource(String url) {
        HikariDataSource dataSource = new HikariDataSource();

        //dataSource.setDriverClassName(className);
        //dataSource.setJdbcUrl(url);

        dataSource.setMaximumPoolSize(maxPoolSize);
        // Hikari Pool is using minIdle as min pool size...
        dataSource.setMinimumIdle(minPoolSize);

        dataSource.setUsername(driverProperties.getProperty("user"));
        dataSource.setPassword(driverProperties.getProperty("password"));

        dataSource.setDataSourceProperties(driverProperties);

        dataSource.setDataSource(new JunboDriverDataSource(
                url,
                driverProperties,
                driverProperties.getProperty("user"),
                driverProperties.getProperty("password")));

        return dataSource;
    }
}
