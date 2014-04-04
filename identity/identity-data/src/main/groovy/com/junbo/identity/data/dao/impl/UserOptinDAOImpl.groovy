/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.UserOptinDAO
import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.identity.spec.options.list.UserOptinListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils

/**
 * Implementation for UserOptinDAO.
 */
@CompileStatic
class UserOptinDAOImpl extends BaseDAO implements UserOptinDAO {

    @Override
    UserOptinEntity save(UserOptinEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    UserOptinEntity update(UserOptinEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    UserOptinEntity get(Long id) {
        return (UserOptinEntity)currentSession(id).get(UserOptinEntity, id)
    }

    @Override
    List<UserOptinEntity> search(Long userId, UserOptinListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserOptinEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (!StringUtils.isEmpty(getOption.type)) {
           criteria.add(Restrictions.eq('type', getOption.type))
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
        UserOptinEntity entity = (UserOptinEntity)currentSession(id).get(UserOptinEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }
}
