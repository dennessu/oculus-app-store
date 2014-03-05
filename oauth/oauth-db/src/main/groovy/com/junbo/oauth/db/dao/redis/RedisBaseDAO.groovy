/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import redis.clients.jedis.Jedis

/**
 * Javadoc.
 */
@CompileStatic
class RedisBaseDAO {
    protected Jedis jedis
    protected String namespace

    @Required
    void setJedis(Jedis jedis) {
        this.jedis = jedis
    }

    @Required
    void setNamespace(String namespace) {
        this.namespace = namespace
    }

}
