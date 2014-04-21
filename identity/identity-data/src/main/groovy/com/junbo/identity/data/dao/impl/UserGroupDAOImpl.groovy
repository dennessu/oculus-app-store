/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.UserGroupDAO
import com.junbo.identity.data.entity.user.UserGroupEntity
import com.junbo.identity.spec.options.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserGroupDAOImpl extends BaseDAO implements UserGroupDAO {
    @Override
    UserGroupEntity save(UserGroupEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId(entity.userId)
        }
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get((Long)entity.id)
    }

    @Override
    UserGroupEntity update(UserGroupEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get((Long)entity.id)
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
    List<UserGroupEntity> findByGroupId(Long groupId, UserGroupListOptions getOption) {
        UserGroupEntity example = new UserGroupEntity()
        example.setGroupId(groupId)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            if (CollectionUtils.isEmpty(ids)) {
                return new ArrayList<UserGroupEntity>()
            }

            def result = []
            ids.each { Long id ->
                result.add(get(id))
            }

            return result
        }

        return new ArrayList<UserGroupEntity>()
    }

    @Override
    void delete(Long id) {
        Session session = currentSession(id)
        UserGroupEntity entity = (UserGroupEntity)session.get(UserGroupEntity, id)
        session.delete(entity)
        session.flush()
    }
}
