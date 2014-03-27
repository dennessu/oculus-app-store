/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserDeviceDAO
import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.spec.options.list.UserDeviceListOptions
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Implementation for UserDeviceDAO.
 */
class UserDeviceDAOImpl extends ShardedDAOBase implements UserDeviceDAO {

    @Override
    UserDeviceEntity save(UserDeviceEntity entity) {
        currentSession().save(entity)
        return get(entity.id)
    }

    @Override
    UserDeviceEntity update(UserDeviceEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserDeviceEntity get(Long id) {
        return (UserDeviceEntity)currentSession().get(UserDeviceEntity, id)
    }

    @Override
    List<UserDeviceEntity> search(Long userId, UserDeviceListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserDeviceEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.deviceId != null) {
            criteria.add('deviceId', getOption.deviceId)
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
        UserDeviceEntity entity = currentSession().get(UserDeviceEntity, id.value)
        currentSession().delete(entity)
    }
}
