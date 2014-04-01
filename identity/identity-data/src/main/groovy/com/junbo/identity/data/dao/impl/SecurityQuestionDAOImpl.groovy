/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.SecurityQuestionDAO
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions
import com.junbo.sharding.IdGeneratorFacade
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.beans.factory.annotation.Autowired
/**
 * Created by liangfu on 3/16/14.
 * Security question persistent in config db, not sharded.
 */
@CompileStatic
class SecurityQuestionDAOImpl implements SecurityQuestionDAO {

    private SessionFactory sessionFactory

    @Autowired
    private IdGeneratorFacade idGenerator

    void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }

    protected Session currentSession() {
        return sessionFactory.currentSession
    }

    @Override
    SecurityQuestionEntity save(SecurityQuestionEntity entity) {
        entity.setId(idGenerator.nextId(UserId))
        currentSession().save(entity)
        return get(entity.id)
    }

    @Override
    SecurityQuestionEntity update(SecurityQuestionEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    SecurityQuestionEntity get(Long id) {
        return (SecurityQuestionEntity)currentSession().get(SecurityQuestionEntity, id)
    }

    @Override
    List<SecurityQuestionEntity> search(SecurityQuestionListOptions listOption) {

        Criteria criteria = currentSession().createCriteria(SecurityQuestionEntity)
        criteria.add(Restrictions.eq('value', listOption.value))
        if (listOption.active != null) {
            criteria.add(Restrictions.eq('active', listOption.active))
        }
        criteria.addOrder(Order.asc('id'))
        if (listOption.limit != null) {
            criteria.setMaxResults(listOption.limit)
        }
        if (listOption.offset != null) {
            criteria.setFirstResult(listOption.offset)
        }
        return criteria.list()
    }

    @Override
    void delete(Long id) {
        SecurityQuestionEntity entity = (SecurityQuestionEntity)currentSession().get(SecurityQuestionEntity, id)
        currentSession().delete(entity)
    }
}
