/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.common.id.UserOptInId
import com.junbo.identity.data.dao.UserOptInDAO
import com.junbo.identity.data.entity.user.UserOptInEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.util.Constants
import com.junbo.identity.spec.model.user.UserOptIn
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.IdGeneratorFacade
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.util.StringUtils
/**
 * Implementation for UserOptInDAO.
 */
class UserOptInDAOImpl implements UserOptInDAO {
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
    UserOptIn save(UserOptIn entity) {
        UserOptInEntity userOptInEntity = modelMapper.toUserOptIn(entity, new MappingContext())
        userOptInEntity.setId(idGenerator.nextId(UserOptInId, userOptInEntity.userId))
        userOptInEntity.setCreatedBy(Constants.DEFAULT_CLIENT_ID)
        userOptInEntity.setCreatedTime(new Date())
        currentSession().save(userOptInEntity)
        get(userOptInEntity.id)
    }

    @Override
    UserOptIn update(UserOptIn entity) {
        UserOptInEntity userOptInEntity = modelMapper.toUserOptIn(entity, new MappingContext())
        UserOptInEntity userOptInInDB = currentSession().get(UserOptInEntity, userOptInEntity.id)
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
    UserOptIn get(Long id) {
        modelMapper.toUserOptIn(currentSession().get(UserOptInEntity, id), new MappingContext())
    }

    @Override
    List<UserOptIn> findByUser(Long userId, String type) {
        def result = []
        List entities = null
        if (StringUtils.isEmpty(type)) {
            entities = currentSession().
                    createSQLQuery('select * from user_optin where user_id = :userId').
                    addEntity(UserOptInEntity).setParameter('userId', userId).list()
        }
        else {
            entities = currentSession().
                    createSQLQuery('select * from user_optin where user_id = :userId and type = :type').
                    addEntity(UserOptInEntity).setParameter('userId', userId).setParameter('type', type).list()
        }

        entities.each { i ->
            result.add(modelMapper.toUserOptIn(i, new MappingContext()))
        }
        result
    }

    @Override
    void delete(Long id) {
        UserOptInEntity entity = currentSession().get(UserOptInEntity, id)
        currentSession().delete(entity)
    }
}
