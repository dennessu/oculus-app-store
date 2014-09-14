package com.junbo.sharding.pool;

import com.junbo.sharding.hibernate.ShardMultiTenantConnectionProviderFactoryBean;
import com.zaxxer.hikari.HikariDataSource;

/**
 * JunboShardMultiTenantConnectionProviderFactoryBean.
 */
public class JunboShardMultiTenantConnectionProviderFactoryBean extends ShardMultiTenantConnectionProviderFactoryBean {
    protected HikariDataSource createDataSource(String url) {
        JunboHikariDataSource dataSource = new JunboHikariDataSource();

        dataSource.setDriverClassName(className);
        dataSource.setJdbcUrl(url);

        dataSource.setMaximumPoolSize(maxPoolSize);
        // Hikari Pool is using minIdle as min pool size...
        dataSource.setMinimumIdle(minPoolSize);

        dataSource.setUsername(driverProperties.getProperty("user"));
        dataSource.setPassword(driverProperties.getProperty("password"));

        dataSource.setDataSourceProperties(driverProperties);

        return dataSource;
    }
}
