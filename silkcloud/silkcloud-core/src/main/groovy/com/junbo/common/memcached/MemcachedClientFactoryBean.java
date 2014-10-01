/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.memcached;

import net.spy.memcached.*;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Java doc.
 */
public class MemcachedClientFactoryBean implements InitializingBean, FactoryBean<MemcachedClientIF> {

    private String servers;
    private ConnectionFactoryBuilder connectionFactoryBuilder;
    private ConnectionFactory connectionFactory;

    @Required
    public void setServers(String servers) {
        this.servers = servers;
    }

    @Required
    public void setConnectionFactoryBuilder(ConnectionFactoryBuilder connectionFactoryBuilder) {
        this.connectionFactoryBuilder = connectionFactoryBuilder;
    }

    @Override
    public MemcachedClientIF getObject() throws Exception {
        return new MemcachedClient(connectionFactory, AddrUtil.getAddresses(servers));
    }

    @Override
    public Class<?> getObjectType() {
        return MemcachedClientIF.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        connectionFactory = connectionFactoryBuilder.build();
    }
}
