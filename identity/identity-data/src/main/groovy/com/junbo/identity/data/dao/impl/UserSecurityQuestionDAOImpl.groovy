/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserSecurityQuestionDAO
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserSecurityQuestionDAOImpl extends BaseDAO implements UserSecurityQuestionDAO {
    @Override
    UserSecurityQuestionEntity save(UserSecurityQuestionEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionEntity update(UserSecurityQuestionEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionEntity get(Long id) {
        return (UserSecurityQuestionEntity)currentSession(id).get(UserSecurityQuestionEntity, id)
    }

    @Override
    List<UserSecurityQuestionEntity> search(Long userId, UserSecurityQuestionListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserSecurityQuestionEntity)
        criteria.add(Restrictions.eq('userId', userId))
        criteria.addOrder(Order.asc('id'))
        if (getOption.limit != null) {
            criteria.setMaxResults(getOption.limit)
        }
        if (getOption.offset != null) {
            criteria.setFirstResult(getOption.offset)
        }
        return criteria.list()
    }

    @Override
    void delete(Long id) {
        Session session = currentSession(id)
        UserSecurityQuestionEntity entity = (UserSecurityQuestionEntity)session.get(UserSecurityQuestionEntity, id)
        session.delete(entity)
        session.flush()
    }
}
