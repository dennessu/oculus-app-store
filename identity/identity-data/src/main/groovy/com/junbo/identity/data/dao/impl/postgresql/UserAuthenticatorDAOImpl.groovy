/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.spec.options.list.UserAuthenticatorListOptions
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Implementation for UserAuthenticatorDAO.
 */
class UserAuthenticatorDAOImpl extends ShardedDAOBase implements UserAuthenticatorDAO {
    @Override
    UserAuthenticatorEntity save(UserAuthenticatorEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    UserAuthenticatorEntity update(UserAuthenticatorEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserAuthenticatorEntity get(Long id) {
        return (UserAuthenticatorEntity)currentSession().get(UserAuthenticatorEntity, id)
    }

    @Override
    List<UserAuthenticatorEntity> search(Long userId, UserAuthenticatorListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserAuthenticatorEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.type != null) {
            criteria.add('type', getOption.type)
        }
        if (getOption.value != null) {
            criteria.add('value', getOption.value)
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
        UserAuthenticatorEntity entity = currentSession().get(UserAuthenticatorEntity, id)
        currentSession().delete(entity)
    }
}
