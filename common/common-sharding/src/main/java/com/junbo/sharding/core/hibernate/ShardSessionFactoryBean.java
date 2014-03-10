/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.hibernate;

import com.junbo.sharding.core.ds.ShardDataSourceFactory;
import com.junbo.sharding.core.ds.ShardDataSourceRegistry;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by minhao on 3/8/14.
 */
public class ShardSessionFactoryBean extends LocalSessionFactoryBean {

    private ShardDataSourceRegistry registry;

    public void setRegistry(ShardDataSourceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public SessionFactory getObject() {
        SessionFactory sf = super.getObject();
        return new ShardSessionFactoryImpl(sf, this.registry);
    }

    /**
     * We need the sessionFactory returned by ShardSessionFactoryBean#getObject() is not singleton. That is,
     * always return independent instances of SessionFactory
     */
    @Override
    public boolean isSingleton() {
        return false;
    }
}
