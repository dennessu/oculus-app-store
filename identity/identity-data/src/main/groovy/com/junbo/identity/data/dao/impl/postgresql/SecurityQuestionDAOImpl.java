/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.SecurityQuestionDAO;
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity;
import com.junbo.identity.spec.model.options.DomainDataGetOption;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class SecurityQuestionDAOImpl implements SecurityQuestionDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Override
    public SecurityQuestionEntity save(SecurityQuestionEntity entity) {
        sessionFactory.getCurrentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public SecurityQuestionEntity update(SecurityQuestionEntity entity) {
        sessionFactory.getCurrentSession().merge(entity);
        sessionFactory.getCurrentSession().flush();

        return get(entity.getId());
    }

    @Override
    public SecurityQuestionEntity get(Long id) {
        return (SecurityQuestionEntity)sessionFactory.getCurrentSession().get(SecurityQuestionEntity.class, id);
    }

    @Override
    public List<SecurityQuestionEntity> search(DomainDataGetOption getOption) {
        String query = "select * from security_question where value like \'%" + getOption.getValue() + "%\'" +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(SecurityQuestionEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        SecurityQuestionEntity entity = (SecurityQuestionEntity)sessionFactory.getCurrentSession()
                .get(SecurityQuestionEntity.class, id);
        sessionFactory.getCurrentSession().delete(entity);
    }
}
