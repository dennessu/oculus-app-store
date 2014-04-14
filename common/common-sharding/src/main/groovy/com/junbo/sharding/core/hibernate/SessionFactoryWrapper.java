/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.hibernate;

import com.junbo.sharding.core.ds.DataSourceConfig;
import com.junbo.sharding.core.ds.ShardDataSourceKey;
import com.junbo.sharding.core.ds.ShardDataSourceMapper;
import com.junbo.sharding.core.ds.ShardDataSourceRegistry;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
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
    private ShardDataSourceMapper mapper;
    private ShardDataSourceRegistry dataSourceRegistry;
    private String sessionFactoryBeanName;
    private String dataBaseName;
    private String[] packagesToScan;
    private Interceptor entityInterceptor;


    private PostInsertEventListener[] postInsertEventListeners;

    private PostUpdateEventListener[] postUpdateEventListeners;

    private PostDeleteEventListener[] postDeleteEventListeners;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setMapper(ShardDataSourceMapper mapper) {
        this.mapper = mapper;
    }

    public void setDataSourceRegistry(ShardDataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }

    public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
        this.sessionFactoryBeanName = sessionFactoryBeanName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public void setPackagesToScan(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setEntityInterceptor(Interceptor entityInterceptor) {
        this.entityInterceptor = entityInterceptor;
    }

    public void setPostInsertEventListeners(PostInsertEventListener[] postInsertEventListeners) {
        this.postInsertEventListeners = postInsertEventListeners;
    }

    public void setPostUpdateEventListeners(PostUpdateEventListener[] postUpdateEventListeners) {
        this.postUpdateEventListeners = postUpdateEventListeners;
    }

    public void setPostDeleteEventListeners(PostDeleteEventListener[] postDeleteEventListeners) {
        this.postDeleteEventListeners = postDeleteEventListeners;
    }

    private Map<Integer, SessionFactory> cache = new HashMap<Integer, SessionFactory>();

    public SessionFactory resolve(int shardId) {
        if (cache.containsKey(shardId)) {
            return cache.get(shardId);
        }

        SessionFactory sf = this.createShardedSessionFactory(shardId);
        cache.put(shardId, sf);
        return sf;
    }

    private SessionFactory createShardedSessionFactory(int shardId) {
        if (this.applicationContext == null) {
            throw new RuntimeException("applicationContext is null in SessionFactoryWrapper!");
        }
        if (this.dataSourceRegistry == null) {
            throw new RuntimeException("dataSourceRegistry is null in SessionFactoryWrapper");
        }
        if (this.mapper == null) {
            throw new RuntimeException("mapper is null in SessionFactoryWrapper");
        }

        DataSourceConfig dc = this.mapper.getDataSourceConfigByShardId(shardId);
        DataSource ds = this.dataSourceRegistry.resolve(shardId,
                new ShardDataSourceKey(dc.getRange().toString(), this.dataBaseName, dc.getLoginRole()));
        Object object = this.applicationContext.getBean("&" + sessionFactoryBeanName);
        LocalSessionFactoryBean factoryBean = null;
        if (object instanceof LocalSessionFactoryBean) {
            try {
                factoryBean = (LocalSessionFactoryBean)object;
                factoryBean.setDataSource(ds);
                factoryBean.setPackagesToScan(packagesToScan);
                factoryBean.afterPropertiesSet();
                factoryBean.setEntityInterceptor(entityInterceptor);
            }
            catch (Exception e) {
                throw new RuntimeException("create sharded sessionFactoryBean(LocalSessionFactoryBean) failed!", e);
            }
        }

        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) factoryBean.getObject();

        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        if (postInsertEventListeners != null) {
            registry.appendListeners(EventType.POST_INSERT, postInsertEventListeners);
        }

        if (postUpdateEventListeners != null) {
            registry.appendListeners(EventType.POST_UPDATE, postUpdateEventListeners);
        }

        if (postDeleteEventListeners != null) {
            registry.appendListeners(EventType.POST_DELETE, postDeleteEventListeners);
        }

        return sessionFactory;
    }
}
