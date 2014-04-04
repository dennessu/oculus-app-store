/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.UserDeviceDAO
import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.spec.options.list.UserDeviceListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Implementation for UserDeviceDAO.
 */
@CompileStatic
class UserDeviceDAOImpl extends BaseDAO implements UserDeviceDAO {

    @Override
    UserDeviceEntity save(UserDeviceEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()
        return get(entity.id)
    }

    @Override
    UserDeviceEntity update(UserDeviceEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
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
        UserDeviceEntity entity = (UserDeviceEntity)currentSession(id).get(UserDeviceEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }
}
