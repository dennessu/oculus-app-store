/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.UserGroupDAO
import com.junbo.identity.data.entity.user.UserGroupEntity
import com.junbo.identity.spec.options.list.UserGroupListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserGroupDAOImpl extends BaseDAO implements UserGroupDAO {
    @Override
    UserGroupEntity save(UserGroupEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    UserGroupEntity update(UserGroupEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    UserGroupEntity get(Long id) {
        return (UserGroupEntity)currentSession(id).get(UserGroupEntity, id)
    }

    @Override
    List<UserGroupEntity> search(Long userId, UserGroupListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserGroupEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.groupId != null) {
            criteria.add(Restrictions.eq('groupId', getOption.groupId.value))
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
        UserGroupEntity entity = (UserGroupEntity)currentSession(id).get(UserGroupEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }
}
