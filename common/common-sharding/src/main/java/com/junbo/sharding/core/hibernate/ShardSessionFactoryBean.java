/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.hibernate;

import com.junbo.sharding.core.ds.ShardDataSourceRegistry;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 * Created by minhao on 3/8/14.
 */
public class ShardSessionFactoryBean extends LocalSessionFactoryBean implements ApplicationContextAware {

    private ShardDataSourceRegistry registry;
    private ApplicationContext applicationContext;

    public void setRegistry(ShardDataSourceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public SessionFactory getObject() {
        SessionFactory sf = super.getObject();
        ShardSessionFactoryImpl impl = new ShardSessionFactoryImpl(sf, this.registry);
        impl.setApplicationContext(this.applicationContext);
        return impl;
    }

    /**
     * We need the sessionFactory returned by ShardSessionFactoryBean#getObject() is not singleton. That is,
     * always return independent instances of SessionFactory
     */
    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
