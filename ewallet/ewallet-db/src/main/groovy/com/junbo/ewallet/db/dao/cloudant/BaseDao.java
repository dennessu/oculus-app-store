/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.ewallet.db.entity.Entity;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

/**
 * baseDao for entity.
 */
public abstract class BaseDao<T extends Entity> extends CloudantClient<T> {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    private Class<T> entityType;

    public T get(Long id) {
        return super.cloudantGet(id.toString()).get();
    }

    public T insert(T t) {
        Date now = new Date();
        t.setpId(generateId(t.getShardMasterId()));
        t.setUpdatedBy(123L);   //TODO
        t.setUpdatedTime(now);
        return super.cloudantPost(t).get();
    }

    public T update(T t) {
        return super.cloudantPut(t).get();
    }

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(shardId);
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
