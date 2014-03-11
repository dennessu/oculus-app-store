/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.hibernate;

import com.junbo.sharding.core.ds.ShardDataSourceKey;
import com.junbo.sharding.core.ds.ShardDataSourceRegistry;
import com.junbo.sharding.core.util.PackagesToScanMapper;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haomin on 14-3-11.
 */
public class SessionFactoryWrapper implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private ShardDataSourceRegistry dataSourceRegistry;
    private String sessionFactoryBeanName;
    private PackagesToScanMapper packagesToScanMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setDataSourceRegistry(ShardDataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }

    public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
        this.sessionFactoryBeanName = sessionFactoryBeanName;
    }

    public void setPackagesToScanMapper(PackagesToScanMapper packagesToScanMapper) {
        this.packagesToScanMapper = packagesToScanMapper;
    }

    private Map<ShardDataSourceKey, SessionFactory> cache = new HashMap<ShardDataSourceKey, SessionFactory>();

    public SessionFactory resolve(ShardDataSourceKey key) {
        if (key == null) {
            return null;
        }
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        SessionFactory sf = this.createShardedSessionFactory(key.getShardId(), key.getDatabaseName(),
                packagesToScanMapper.getPackagesToScan(key.getDatabaseName()));

        cache.put(key, sf);
        return sf;
    }

    private SessionFactory createShardedSessionFactory(int shardId, String dbName, String... packagesToScan) {
        if (this.applicationContext == null) {
            throw new RuntimeException("applicationContext is null in SessionFactoryWrapper!");
        }
        if (this.dataSourceRegistry == null) {
            throw new RuntimeException("dataSourceRegistry is null in SessionFactoryWrapper");
        }

        DataSource ds = this.dataSourceRegistry.resolve(new ShardDataSourceKey(shardId, dbName));
        Object object = this.applicationContext.getBean("&" + sessionFactoryBeanName);
        LocalSessionFactoryBean factoryBean = null;
        if (object instanceof LocalSessionFactoryBean) {
            try {
                factoryBean = (LocalSessionFactoryBean)object;
                factoryBean.setDataSource(ds);
                factoryBean.setPackagesToScan(packagesToScan);
                factoryBean.afterPropertiesSet();
            }
            catch (Exception e) {
                throw new RuntimeException("create sharded sessionFactoryBean(LocalSessionFactoryBean) failed!", e);
            }
        }

        return factoryBean.getObject();
    }
}
