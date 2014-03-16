/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.common.id.UserOptinId
import com.junbo.identity.data.dao.UserOptinDAO
import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.util.Constants
import com.junbo.identity.spec.model.users.UserOptin
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.IdGeneratorFacade
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.util.StringUtils
/**
 * Implementation for UserOptinDAO.
 */
class UserOptinDAOImpl implements UserOptinDAO {
    @Autowired
    @Qualifier('sessionFactory')
    private SessionFactory sessionFactory

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    private IdGeneratorFacade idGenerator

    private Session currentSession() {
        sessionFactory.currentSession
    }

    @Override
    UserOptin save(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        userOptInEntity.setId(idGenerator.nextId(UserOptinId, userOptInEntity.userId))
        userOptInEntity.setCreatedBy(Constants.DEFAULT_CLIENT_ID)
        userOptInEntity.setCreatedTime(new Date())
        currentSession().save(userOptInEntity)
        get(userOptInEntity.id)
    }

    @Override
    UserOptin update(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        UserOptinEntity userOptInInDB = currentSession().get(UserOptinEntity, userOptInEntity.id)
        currentSession().evict(userOptInInDB)

        userOptInEntity.setCreatedBy(userOptInInDB.createdBy)
        userOptInEntity.setCreatedTime(userOptInInDB.createdTime)
        userOptInEntity.setUpdatedBy(Constants.DEFAULT_CLIENT_ID)
        userOptInEntity.setUpdatedTime(new Date())
        currentSession().update(userOptInEntity)
        currentSession().flush()

        return get(userOptInEntity.id)
    }

    @Override
    UserOptin get(Long id) {
        modelMapper.toUserOptin(currentSession().get(UserOptinEntity, id), new MappingContext())
    }

    @Override
    List<UserOptin> findByUser(Long userId, String type) {
        def result = []
        List entities = null
        if (StringUtils.isEmpty(type)) {
            entities = currentSession().
                    createSQLQuery('select * from user_optin where user_id = :userId').
                    addEntity(UserOptinEntity).setParameter('userId', userId).list()
        }
        else {
            entities = currentSession().
                    createSQLQuery('select * from user_optin where user_id = :userId and type = :type').
                    addEntity(UserOptinEntity).setParameter('userId', userId).setParameter('type', type).list()
        }

        entities.each { i ->
            result.add(modelMapper.toUserOptin(i, new MappingContext()))
        }
        result
    }

    @Override
    void delete(Long id) {
        UserOptinEntity entity = currentSession().get(UserOptinEntity, id)
        currentSession().delete(entity)
    }
}
