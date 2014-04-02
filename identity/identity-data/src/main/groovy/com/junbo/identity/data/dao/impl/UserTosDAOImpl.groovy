/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.UserTosDAO
import com.junbo.identity.data.entity.user.UserTosEntity
import com.junbo.identity.spec.options.list.UserTosListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
@CompileStatic
class UserTosDAOImpl extends ShardedDAOBase implements UserTosDAO {
    @Override
    UserTosEntity save(UserTosEntity entity) {
        currentSession().save(entity)
        return get(entity.id)
    }

    @Override
    UserTosEntity update(UserTosEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserTosEntity get(Long id) {
        return (UserTosEntity)currentSession().get(UserTosEntity, id)
    }

    @Override
    List<UserTosEntity> search(Long userId, UserTosListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserTosEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (!StringUtils.isEmpty(getOption.tosUri)) {
            criteria.add(Restrictions.eq('tosUri', getOption.tosUri))
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
        UserTosEntity entity = (UserTosEntity)currentSession().get(UserTosEntity, id)
        currentSession().delete(entity)
    }
}
