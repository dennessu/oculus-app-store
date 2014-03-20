/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.dao.SecurityQuestionDAO;
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;
import com.junbo.sharding.IdGeneratorFacade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 * Security question persistent in config db, not sharded.
 */
@Component
public class SecurityQuestionDAOImpl implements SecurityQuestionDAO {

    private SessionFactory sessionFactory;

    @Autowired
    private IdGeneratorFacade idGenerator;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public SecurityQuestionEntity save(SecurityQuestionEntity entity) {
        entity.setId(idGenerator.nextId(UserId.class));
        entity.setCreatedBy("");
        entity.setCreatedTime(new Date());
        currentSession().save(entity);
        return get(entity.getId());
    }

    @Override
    public SecurityQuestionEntity update(SecurityQuestionEntity entity) {
        SecurityQuestionEntity existEntity = get(entity.getId());
        currentSession().evict(existEntity);
        entity.setCreatedTime(existEntity.getCreatedTime());
        entity.setCreatedBy(existEntity.getCreatedBy());
        entity.setResourceAge(existEntity.getResourceAge());
        currentSession().update(entity);
        currentSession().flush();
        return get(entity.getId());
    }

    @Override
    public SecurityQuestionEntity get(Long id) {
        return (SecurityQuestionEntity)currentSession().get(SecurityQuestionEntity.class, id);
    }

    @Override
    public List<SecurityQuestionEntity> search(SecurityQuestionListOptions listOption) {
        String query = "select * from security_question where active = true order by id";
        List entities = currentSession().createSQLQuery(query).addEntity(SecurityQuestionEntity.class).list();
        return entities;
    }

    @Override
    public void delete(Long id) {
        SecurityQuestionEntity entity = (SecurityQuestionEntity)currentSession().get(SecurityQuestionEntity.class, id);
        currentSession().delete(entity);
    }
}
