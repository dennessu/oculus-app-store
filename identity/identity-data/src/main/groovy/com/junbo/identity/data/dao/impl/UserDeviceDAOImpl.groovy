/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserDeviceDAO
import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.CollectionUtils

/**
 * Implementation for UserDeviceDAO.
 */
@CompileStatic
class UserDeviceDAOImpl extends BaseDAO implements UserDeviceDAO {

    @Override
    UserDeviceEntity save(UserDeviceEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()
        return get((Long)entity.id)
    }

    @Override
    UserDeviceEntity update(UserDeviceEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get((Long)entity.id)
    }

    @Override
    UserDeviceEntity get(Long id) {
        return (UserDeviceEntity)currentSession(id).get(UserDeviceEntity, id)
    }

    @Override
    List<UserDeviceEntity> search(Long userId, UserDeviceListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserDeviceEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.deviceId != null) {
            criteria.add(Restrictions.eq('deviceId', getOption.deviceId))
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
        UserDeviceEntity entity = (UserDeviceEntity)session.get(UserDeviceEntity, id)
        session.delete(entity)
        session.flush()
    }

    @Override
    List<UserDeviceEntity> findByDeviceId(Long deviceId, UserDeviceListOptions getOption) {
        UserDeviceEntity example = new UserDeviceEntity()
        example.setDeviceId(deviceId)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            if (CollectionUtils.isEmpty(ids)) {
                return new ArrayList<UserDeviceEntity>()
            }

            def result = []
            ids.each { Long id ->
                result.add(get(id))
            }

            return result
        }

        return new ArrayList<UserDeviceEntity>()
    }
}
