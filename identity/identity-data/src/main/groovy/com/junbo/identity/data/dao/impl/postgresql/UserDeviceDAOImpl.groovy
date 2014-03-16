/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.common.id.UserDeviceId
import com.junbo.identity.data.dao.UserDeviceDAO
import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.spec.model.options.UserDeviceGetOption
import com.junbo.identity.spec.model.users.UserDevice
import com.junbo.oom.core.MappingContext
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Implementation for UserDeviceDAO.
 */
class UserDeviceDAOImpl implements UserDeviceDAO {
    @Autowired
    @Qualifier('sessionFactory')
    private SessionFactory sessionFactory

    @Autowired
    private ModelMapper modelMapper

    private Session currentSession() {
        sessionFactory.currentSession
    }

    @Override
    UserDevice save(UserDevice entity) {
        UserDeviceEntity userDeviceProfileEntity = modelMapper.toUserDevice(entity, new MappingContext())

        currentSession().save(userDeviceProfileEntity)
        return get(userDeviceProfileEntity.id)
    }

    @Override
    UserDevice update(UserDevice entity) {
        UserDeviceEntity userDeviceProfileEntity = modelMapper.toUserDevice(entity, new MappingContext())
        currentSession().evict(currentSession().get(UserDeviceEntity, userDeviceProfileEntity.id))
        currentSession().update(userDeviceProfileEntity)
        currentSession().flush()

        return get(userDeviceProfileEntity.id)
    }

    @Override
    UserDevice get(UserDeviceId id) {
        return modelMapper.toUserDevice(currentSession().get(UserDeviceEntity, id.value), new MappingContext())
    }

    @Override
    List<UserDevice> search(UserDeviceGetOption getOption) {
        def result = []
        String query = 'select * from user_device where user_id =  ' + getOption.userId.value +
                (getOption.deviceId == null ? '' : ' and device_id = ' + getOption.deviceId) +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        def entities = sessionFactory.currentSession.createSQLQuery(query).addEntity(UserDeviceEntity).list()

        entities.flatten { i ->
            result.add(modelMapper.toUserDevice(i, new MappingContext()))
        }
        return result
    }

    @Override
    void delete(UserDeviceId id) {
        UserDeviceEntity entity = currentSession().get(UserDeviceEntity, id.value)
        currentSession().delete(entity)
    }
}
