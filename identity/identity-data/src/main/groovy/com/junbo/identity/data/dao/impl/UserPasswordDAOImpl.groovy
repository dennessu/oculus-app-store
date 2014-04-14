/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.UserPasswordDAO
import com.junbo.identity.data.entity.user.UserPasswordEntity
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Created by liangfu on 3/16/14.
 */
@CompileStatic
class UserPasswordDAOImpl extends BaseDAO implements UserPasswordDAO {

    @Override
    UserPasswordEntity save(UserPasswordEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserPasswordEntity update(UserPasswordEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()
        return get(entity.id)
    }

    @Override
    UserPasswordEntity get(Long id) {
        return (UserPasswordEntity)currentSession(id).get(UserPasswordEntity, id)
    }

    @Override
    List<UserPasswordEntity> search(Long userId, UserPasswordListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserPasswordEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.active != null) {
            criteria.add(Restrictions.eq('active', getOption.active))
        }
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
        UserPasswordEntity entity = (UserPasswordEntity)session.get(UserPasswordEntity, id)
        session.delete(entity)
        session.flush()
    }
}
