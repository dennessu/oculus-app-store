/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.health.ping;

import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.ShardAlgorithm;
import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * The ping repo implemented by SQL.
 */
public class PingSqlRepo {

    private String dbName;
    private SessionFactory sessionFactory;
    private ShardAlgorithm shardAlgorithm;
    private IdGenerator idGenerator;

    @Required
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Required
    public void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm;
    }

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Ping get(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return (Ping)currentSession(id).get(Ping.class, id);
    }

    public Ping create(Ping model) {
        if (model == null) {
            throw new IllegalArgumentException("model is null");
        }
        if (model.getId() == null) {
            model.setId(String.valueOf(idGenerator.nextId()));
        }
        Session session = currentSession(model.getId());
        session.save(model);
        session.flush();

        return model;
    }

    public Ping update(Ping model) {
        if (model == null) {
            throw new IllegalArgumentException("model is null");
        }

        Session session = currentSession(model.getId());
        Ping result = (Ping)session.merge(model);
        session.flush();

        return result;
    }

    public void delete(String id) {
        Session session = currentSession(id);
        Ping entity = (Ping)session.get(Ping.class, id);
        session.delete(entity);
        session.flush();
    }

    public String getDbName() {
        return dbName;
    }

    private Session currentSession(String key) {
        long longKey = Long.parseLong(key);
        try (ShardScope scope = new ShardScope(shardAlgorithm.dataCenterId(longKey), shardAlgorithm.shardId(longKey))) {
            return sessionFactory.getCurrentSession();
        }
    }
}
