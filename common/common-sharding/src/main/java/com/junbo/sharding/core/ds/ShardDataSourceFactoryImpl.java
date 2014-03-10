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
    public DataSource createDataSource(int shardId, String dbName) {
        if (this.applicationContext == null) {
            throw new RuntimeException("applicationContext is null in ShardDataSourceFactory!");
        }
        if (this.mapper == null) {
            throw new RuntimeException("datasource mapper is null in ShardDataSourceFactory!");
        }

        DataSource dataSource = applicationContext.getBean(dataSourceBeanName, DataSource.class);
        if (dataSource == null) {
            throw new RuntimeException(String.format("Can't get prototype dataSource of name [%s] from ApplicationContext", dataSourceBeanName));
        }

        if (dataSource instanceof PoolingDataSource) {
            // reset btmDataSource unique name and url
            PoolingDataSource btmDataSource = (PoolingDataSource)dataSource;
            btmDataSource.setUniqueName(String.format("jdbc/%s_ds_%s", dbName, shardId));

            DataSourceConfig config = mapper.getDataSourceConfigByShardId(shardId);
            String url = config.getJdbcUrlTemplate().replaceFirst("%DB_NAME%", dbName);
            btmDataSource.getDriverProperties().setProperty("url", url);

            return btmDataSource;
        }
        else {
            throw new RuntimeException("Unrecognized dataSource type: " + dataSource.getClass() + ", sharding not support with this type of datasource");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
