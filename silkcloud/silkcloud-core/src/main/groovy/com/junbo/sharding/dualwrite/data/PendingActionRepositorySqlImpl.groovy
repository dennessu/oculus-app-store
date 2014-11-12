/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.data
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.hibernate.ShardScope
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions
import org.springframework.beans.factory.annotation.Required

/**
 * The pending action repo implemented by SQL.
 */
@CompileStatic
public class PendingActionRepositorySqlImpl implements PendingActionRepository {

    private SessionFactory sessionFactory
    private ShardAlgorithm shardAlgorithm
    private boolean hardDelete;
    private Integer maxRetryCount;

    private PendingActionMapper mapper;
    private IdGenerator idGenerator;

    @Required
    void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    void setHardDelete(boolean hardDelete) {
        this.hardDelete = hardDelete
    }

    @Required
    void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setMapper(PendingActionMapper mapper) {
        this.mapper = mapper
    }

    boolean getHardDelete() {
        return hardDelete
    }

    @Override
    public Promise<PendingAction> get(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        PendingActionEntity entity = (PendingActionEntity)currentSession(id).get(PendingActionEntity, id);
        if (entity != null && entity.isDeleted()) {
            return Promise.pure(null);
        }

        return Promise.pure(mapper.map(entity));
    }

    @Override
    public Promise<PendingAction> create(PendingAction model) {
        if (model == null) {
            throw new IllegalArgumentException('model is null')
        }
        if (model.id == null) {
            model.id = idGenerator.nextId(model.getChangedEntityId());
        }
        model.retryCount = 0
        model.createdBy = 123L      // TODO
        model.createdTime = new Date()

        def entity = mapper.map(model)
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get(model.getId())
    }

    @Override
    public Promise<PendingAction> update(PendingAction model, PendingAction oldModel) {
        if (model == null) {
            throw new IllegalArgumentException('model is null')
        }

        def entity = mapper.map(model)
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get(model.getId())
    }

    @Override
    public Promise<Void> delete(Long id) {
        Session session = currentSession(id)
        PendingActionEntity entity = (PendingActionEntity)session.get(PendingActionEntity, id)

        if (hardDelete) {
            session.delete(entity);
        } else {
            entity.setDeleted(true)
            session.merge(entity)
        }
        session.flush()

        return Promise.pure(null);
    }

    @Override
    public Promise<List<PendingAction>> list(Integer dc, Integer shardId, Integer limit, Integer offset, Integer timeOffset) {

        Session session = ShardScope.with(dc, shardId) { sessionFactory.currentSession}

        Criteria criteria = session.createCriteria(PendingActionEntity);
        criteria.add(Restrictions.ne("deleted", Boolean.TRUE));
        criteria.add(Restrictions.le("retryCount", maxRetryCount));
        criteria.add(Restrictions.le("createdTime", calcEndTime(timeOffset)))
        criteria.setFirstResult(offset)
        criteria.setMaxResults(limit)

        List entities = criteria.list()
        List<PendingAction> result = new ArrayList<>()
        entities.each { PendingActionEntity entity ->
            if (entity != null) {
                result.add(mapper.map(entity))
            }
        }

        return Promise.pure(result)
    }

    private Session currentSession(Object key) {
        return ShardScope.with(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key)) { sessionFactory.currentSession }
    }

    static Date calcEndTime(int millionSec) {
        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, -millionSec)
        return calendar.getTime()
    }
}
