/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import groovy.transform.CompileStatic
import net.spy.memcached.MemcachedClientIF
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedBaseDAO {
    protected final static Long MILLION = 1000000L
    protected MemcachedClientIF memcachedClient
    protected String namespace

    @Required
    void setMemcachedClient(MemcachedClientIF memcachedClient) {
        this.memcachedClient = memcachedClient
    }

    @Required
    void setNamespace(String namespace) {
        this.namespace = namespace
    }
}
