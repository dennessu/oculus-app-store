/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserEmailDAO
import com.junbo.identity.data.entity.user.UserEmailEntity
import com.junbo.identity.spec.options.list.UserEmailListOptions
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserEmailDAOImpl extends BaseDAO implements UserEmailDAO {

    @Override
    void delete(Long id) {
        UserEmailEntity entity = (UserEmailEntity)currentSession(id).get(UserEmailEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }

    @Override
    List<UserEmailEntity> search(Long userId, UserEmailListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserEmailEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (!StringUtils.isEmpty(getOption.type)) {
            criteria.add(Restrictions.eq('type', getOption.type))
        }
        if (!StringUtils.isEmpty(getOption.value)) {
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
    UserEmailEntity get(Long id) {
        return (UserEmailEntity)currentSession(id).get(UserEmailEntity, id)
    }

    @Override
    UserEmailEntity update(UserEmailEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get((Long)(entity.id))
    }

    @Override
    UserEmailEntity save(UserEmailEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)
        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()

        return get((Long)(entity.id))
    }

    @Override
    UserEmailEntity findIdByEmail(String email) {
        UserEmailEntity example = new UserEmailEntity()
        example.setValue(email)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            Long id = CollectionUtils.isEmpty(ids) ? null : (Long)(ids.get(0))
            if (id == null) {
                return get(id)
            }
        }

        return null
    }
}
