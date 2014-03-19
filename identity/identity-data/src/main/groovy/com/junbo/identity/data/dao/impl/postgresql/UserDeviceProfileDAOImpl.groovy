/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.common.id.UserDeviceProfileId
import com.junbo.identity.data.dao.UserDeviceProfileDAO
import com.junbo.identity.data.entity.user.UserDeviceProfileEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.util.Constants
import com.junbo.identity.spec.model.user.UserDeviceProfile
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.IdGeneratorFacade
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.util.StringUtils
/**
 * Implementation for UserDeviceProfileDAO.
 */
class UserDeviceProfileDAOImpl implements UserDeviceProfileDAO {
    @Autowired
    @Qualifier('identitySessionFactory')
    private SessionFactory sessionFactory

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    private IdGeneratorFacade idGenerator

    private Session currentSession() {
        sessionFactory.currentSession
    }

    @Override
    UserDeviceProfile save(UserDeviceProfile entity) {
        UserDeviceProfileEntity userDeviceProfileEntity = modelMapper.toUserDeviceProfile(entity, new MappingContext())
        userDeviceProfileEntity.setId(idGenerator.nextId(UserDeviceProfileId, userDeviceProfileEntity.userId))
        userDeviceProfileEntity.setCreatedTime(new Date())
        userDeviceProfileEntity.setCreatedBy(Constants.DEFAULT_CLIENT_ID)
        currentSession().save(userDeviceProfileEntity)
        get(userDeviceProfileEntity.id)
    }

    @Override
    UserDeviceProfile update(UserDeviceProfile entity) {
        UserDeviceProfileEntity userDeviceProfileEntity = modelMapper.toUserDeviceProfile(entity, new MappingContext())
        UserDeviceProfileEntity entityInDB = currentSession().get(UserDeviceProfileEntity, userDeviceProfileEntity.id)
        currentSession().evict(entityInDB)

        userDeviceProfileEntity.setCreatedBy(entityInDB.createdBy)
        userDeviceProfileEntity.setCreatedTime(entityInDB.createdTime)
        userDeviceProfileEntity.setUpdatedTime(new Date())
        userDeviceProfileEntity.setUpdatedBy(Constants.DEFAULT_CLIENT_ID)
        currentSession().update(userDeviceProfileEntity)
        currentSession().flush()

        return get(userDeviceProfileEntity.id)
    }

    @Override
    UserDeviceProfile get(Long id) {
        modelMapper.toUserDeviceProfile(currentSession().get(UserDeviceProfileEntity, id), new MappingContext())
    }

    @Override
    List<UserDeviceProfile> findByUser(Long userId, String type) {
        def result = []

        List entities = null
        if (StringUtils.isEmpty(type)) {
            entities = currentSession().
                    createSQLQuery('select * from user_device_profile where user_id = :userId').
                    addEntity(UserDeviceProfileEntity).setParameter('userId', userId).list()
        }
        else {
            entities = currentSession().
                    createSQLQuery('select * from user_device_profile where user_id = :userId and type = :type').
                    addEntity(UserDeviceProfileEntity).setParameter('userId', userId).setParameter('type', type).list()
        }

        entities.each { i ->
            result.add(modelMapper.toUserDeviceProfile(i, new MappingContext()))
        }
        result
    }

    @Override
    void delete(Long id) {
        UserDeviceProfileEntity entity = currentSession().get(UserDeviceProfileEntity, id)
        currentSession().delete(entity)
    }
}
