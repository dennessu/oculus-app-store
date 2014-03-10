/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.hibernate;

import com.junbo.sharding.core.ds.ShardDataSourceFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import java.io.IOException;

/**
 * Created by minhao on 3/8/14.
 */
public class ShardSessionFactoryBean extends LocalSessionFactoryBean {

    private ShardDataSourceFactory shardedDataSourceFactory;

    public void setShardedDataSourceFactory(ShardDataSourceFactory shardedDataSourceFactory) {
        this.shardedDataSourceFactory = shardedDataSourceFactory;
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        // reset datasource before creating a new sessionFactory
        super.setDataSource(this.shardedDataSourceFactory.createDataSource());

        super.afterPropertiesSet();
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
