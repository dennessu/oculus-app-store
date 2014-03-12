/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;

/**
 * Created by minhao on 3/8/14.
 */
public class ShardDataSourceFactoryImpl implements ShardDataSourceFactory, ApplicationContextAware {

    private String dataSourceBeanName;
    private ApplicationContext applicationContext;
    private ShardDataSourceMapper mapper;

    public void setDataSourceBeanName(String dataSourceBeanName) { this.dataSourceBeanName = dataSourceBeanName; }
    public void setMapper(ShardDataSourceMapper mapper) { this.mapper = mapper; }

    @Override
    public DataSource createDataSource(ShardDataSourceKey key) {
        if (this.applicationContext == null) {
            throw new RuntimeException("applicationContext is null in ShardDataSourceFactory!");
        }
        if (this.mapper == null) {
            throw new RuntimeException("datasource mapper is null in ShardDataSourceFactory!");
        }

        DataSource dataSource = applicationContext.getBean(dataSourceBeanName, DataSource.class);
        if (dataSource == null) {
            throw new RuntimeException(String.format(
                    "Can't get prototype dataSource of name [%s] from ApplicationContext", dataSourceBeanName));
        }

        if (dataSource instanceof PoolingDataSource) {
            // reset btmDataSource unique name and url
            PoolingDataSource btmDataSource = (PoolingDataSource)dataSource;
            btmDataSource.setUniqueName(String.format("jdbc/%s_ds_%s", key.getDatabaseName(), key.getShardId()));

            DataSourceConfig config = mapper.getDataSourceConfigByShardId(key.getShardId());
            String url = config.getJdbcUrlTemplate().replaceFirst("%DB_NAME%", key.getDatabaseName());
            btmDataSource.getDriverProperties().setProperty("url", url);
            btmDataSource.getDriverProperties().setProperty("user", "shard_"+key.getShardId());
            btmDataSource.init();

            return btmDataSource;
        }
        else {
            throw new RuntimeException("Unrecognized dataSource type: " + dataSource.getClass()
                    + ", sharding not support with this type of datasource");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
