/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.apphost.core

import groovy.transform.CompileStatic
import org.glassfish.grizzly.threadpool.AbstractThreadPool
import org.glassfish.grizzly.threadpool.JunboThreadPool
import org.glassfish.grizzly.threadpool.ThreadPoolConfig
import org.springframework.beans.factory.FactoryBean

import java.util.concurrent.TimeUnit

/**
 * JunboThreadPoolFactoryBean.
 */
@CompileStatic
class JunboThreadPoolFactoryBean implements FactoryBean<JunboThreadPool> {

    private int corePoolSize = AbstractThreadPool.DEFAULT_MIN_THREAD_COUNT

    private int maxPoolSize = AbstractThreadPool.DEFAULT_MAX_THREAD_COUNT

    private int keepAliveTimeMillis = AbstractThreadPool.DEFAULT_IDLE_THREAD_KEEPALIVE_TIMEOUT

    private int queueLimit = AbstractThreadPool.DEFAULT_MAX_TASKS_QUEUED

    private String poolName = "JunboThreadPool"

    void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize
    }

    void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize
    }

    void setKeepAliveTimeMillis(int keepAliveTimeMillis) {
        this.keepAliveTimeMillis = keepAliveTimeMillis
    }

    void setQueueLimit(int queueLimit) {
        this.queueLimit = queueLimit
    }

    void setPoolName(String poolName) {
        this.poolName = poolName
    }

    @Override
    JunboThreadPool getObject() throws Exception {
        ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.defaultConfig()

        threadPoolConfig.poolName = poolName
        threadPoolConfig.corePoolSize = corePoolSize
        threadPoolConfig.maxPoolSize = maxPoolSize
        threadPoolConfig.setKeepAliveTime(keepAliveTimeMillis, TimeUnit.MILLISECONDS)
        threadPoolConfig.queueLimit = queueLimit

        return new JunboThreadPool(threadPoolConfig)
    }

    @Override
    Class<?> getObjectType() {
        return JunboThreadPool
    }

    @Override
    boolean isSingleton() {
        return true
    }
}
