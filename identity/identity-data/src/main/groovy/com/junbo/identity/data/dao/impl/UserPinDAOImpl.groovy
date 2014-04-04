/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.UserPinDAO
import com.junbo.identity.data.entity.user.UserPinEntity
import com.junbo.identity.spec.options.list.UserPinListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Created by liangfu on 3/16/14.
 */
@CompileStatic
class UserPinDAOImpl extends BaseDAO implements UserPinDAO {
    @Override
    UserPinEntity save(UserPinEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()
        return get(entity.id)
    }

    @Override
    UserPinEntity update(UserPinEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    UserPinEntity get(Long id) {
        return (UserPinEntity)currentSession(id).get(UserPinEntity, id)
    }

    @Override
    List<UserPinEntity> search(Long userId, UserPinListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserPinEntity)
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
        UserPinEntity entity = (UserPinEntity)currentSession(id).get(UserPinEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }
}
