/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserDeviceDAO
import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.spec.options.list.UserDeviceListOption
import org.springframework.util.StringUtils

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
    List<UserDeviceEntity> search(Long userId, UserDeviceListOption getOption) {
        String query = 'select * from user_device where user_id =  ' + getOption.userId.value +
                (StringUtils.isEmpty(getOption.deviceId) ? '' : ' and device_id = \'' + getOption.deviceId + '\'') +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        def entities = currentSession().createSQLQuery(query).addEntity(UserDeviceEntity).list()

        return entities
    }

    @Override
    void delete(Long id) {
        UserDeviceEntity entity = currentSession().get(UserDeviceEntity, id.value)
        currentSession().delete(entity)
    }
}
