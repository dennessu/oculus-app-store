/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.spec.options.list.UserAuthenticatorListOptions
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Implementation for UserAuthenticatorDAO.
 */
@CompileStatic
class UserAuthenticatorDAOImpl extends BaseDAO implements UserAuthenticatorDAO {
    @Override
    UserAuthenticatorEntity save(UserAuthenticatorEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()
        return get((Long)entity.id)
    }

    @Override
    UserAuthenticatorEntity update(UserAuthenticatorEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get((Long)entity.id)
    }

    @Override
    UserAuthenticatorEntity get(Long id) {
        return (UserAuthenticatorEntity)currentSession(id).get(UserAuthenticatorEntity, id)
    }

    @Override
    List<UserAuthenticatorEntity> search(Long userId, UserAuthenticatorListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserAuthenticatorEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.type != null) {
            criteria.add(Restrictions.eq('type', getOption.type))
        }
        if (getOption.value != null) {
            criteria.add(Restrictions.eq('value', getOption.value))
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
    UserAuthenticatorEntity getIdByAuthenticatorValue(String value) {
        UserAuthenticatorEntity example = new UserAuthenticatorEntity()
        example.setValue(value)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            Long id = CollectionUtils.isEmpty(ids) ? null : (Long)(ids.get(0))

            if (id != null) {
                return get(id)
            }
        }

        return null
    }

    @Override
    void delete(Long id) {
        UserAuthenticatorEntity entity = (UserAuthenticatorEntity)currentSession(id).get(UserAuthenticatorEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }
}
