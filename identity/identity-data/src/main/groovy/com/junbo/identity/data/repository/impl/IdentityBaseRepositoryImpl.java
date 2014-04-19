/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.Id;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.data.repository.IdentityBaseRepository;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.ShardAlgorithm;
import org.springframework.beans.factory.annotation.Required;

import java.lang.reflect.ParameterizedType;

/**
 * IdentityBaseRepositoryImpl.
 * @param <ID>
 * @param <M>
 * @param <R>
 */
public abstract class IdentityBaseRepositoryImpl<ID extends Id, M extends Identifiable<ID>,
                    R extends IdentityBaseRepository<M, ID>> implements IdentityBaseRepository<M, ID> {
    private R cloudantRepo;
    private R sqlRepo;
    private PersistentMode mode;
    private ShardAlgorithm shardAlgorithm;
    private IdGenerator idGenerator;

    @Required
    public void setMode(PersistentMode mode) {
        this.mode = mode;
    }

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Required
    public void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm;
    }

    @Required
    public void setCloudantRepo(R cloudantRepo) {
        this.cloudantRepo = cloudantRepo;
    }

    @Required
    public void setSqlRepo(R sqlRepo) {
        this.sqlRepo = sqlRepo;
    }

    public Promise<M> get(ID id) {
        if (mode.equals(PersistentMode.SQL_READ_WRITE)) {
            return this.sqlRepo.get(id);
        }
        else {
            // OTHERWISE READ FROM CLOUDANT
            return this.cloudantRepo.get(id);
        }
    }

    public Promise<M> create(M model) {
        Class idClazz = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        ID id = null;
        try {
            id = (ID) idClazz.getDeclaredConstructor(Long.class)
                    .newInstance(idGenerator.nextIdByShardId(shardAlgorithm.shardId()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        model.setId(id);
        if (mode.equals(PersistentMode.SQL_READ_WRITE)) {
            this.sqlRepo.create(model);
        }
        else if (mode.equals(PersistentMode.CLOUDANT_READ_WRITE)) {
            this.cloudantRepo.create(model);
        }
        else if (mode.equals(PersistentMode.CLOUDANT_READ_DUAL_WRITE_CLOUDANT_PRIMARY)) {
            this.cloudantRepo.create(model);
            this.sqlRepo.create(model);
        }
        return null;
    }

    public Promise<M> update(M model) {
        return null;
    }

    public Promise<Void> delete(ID id) {
        if (mode.equals(PersistentMode.CLOUDANT_READ_WRITE)) {
            this.cloudantRepo.delete(id);
        } else if (mode.equals(PersistentMode.CLOUDANT_READ_DUAL_WRITE_SQL_PRIMARY)
                || mode.equals(PersistentMode.CLOUDANT_READ_DUAL_WRITE_CLOUDANT_PRIMARY)) {
            this.sqlRepo.delete(id);
            this.cloudantRepo.delete(id);
        }

        return Promise.pure(null);
    }
}
