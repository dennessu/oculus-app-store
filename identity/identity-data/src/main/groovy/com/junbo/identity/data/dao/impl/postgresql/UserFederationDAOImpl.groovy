/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.common.id.UserFederationId
import com.junbo.identity.data.dao.UserFederationDAO
import com.junbo.identity.data.entity.user.UserFederationEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.util.Constants
import com.junbo.identity.spec.model.user.UserFederation
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.core.hibernate.SessionFactoryWrapper
import com.junbo.sharding.util.Helper
import org.hibernate.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils

/**
 * Implementation for UserFederationDAO.
 */
class UserFederationDAOImpl implements UserFederationDAO {
    @Autowired
    private ModelMapper modelMapper

    private SessionFactoryWrapper sessionFactoryWrapper

    void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper
    }

    private Session currentSession() {
        return sessionFactoryWrapper.resolve(Helper.currentThreadLocalShardId).currentSession
    }

    @Override
    UserFederation save(UserFederation entity) {
        UserFederationEntity userFederationEntity = modelMapper.toUserFederation(entity, new MappingContext())
        userFederationEntity.setId(idGenerator.nextId(UserFederationId, userFederationEntity.userId))
        userFederationEntity.setCreatedBy(Constants.DEFAULT_CLIENT_ID)
        userFederationEntity.setCreatedTime(new Date())
        currentSession().save(userFederationEntity)
        get(userFederationEntity.id)
    }

    @Override
    UserFederation update(UserFederation entity) {
        UserFederationEntity userFederationEntity = modelMapper.toUserFederation(entity, new MappingContext())
        UserFederationEntity entityInDB = currentSession().get(UserFederationEntity, userFederationEntity.id)
        currentSession().evict(entityInDB)
        userFederationEntity.setCreatedTime(entityInDB.createdTime)
        userFederationEntity.setCreatedBy(entityInDB.createdBy)
        userFederationEntity.setUpdatedBy(Constants.DEFAULT_CLIENT_ID)
        userFederationEntity.setUpdatedTime(new Date())
        currentSession().update(userFederationEntity)
        currentSession().flush()

        return get(userFederationEntity.id)
    }

    @Override
    UserFederation get(Long id) {
        modelMapper.toUserFederation(currentSession().get(UserFederationEntity, id), new MappingContext())
    }

    @Override
    List<UserFederation> findByUser(Long userId, String type) {
        def result = []
        List entities = null
        if (StringUtils.isEmpty(type)) {
            entities = currentSession().
                    createSQLQuery('select * from user_federation where user_id = :userId').
                    addEntity(UserFederationEntity).setParameter('userId', userId).list()
        }
        else {
            entities = currentSession().
                    createSQLQuery('select * from user_federation where user_id = :userId and type = :type').
                    addEntity(UserFederationEntity).setParameter('userId', userId).setParameter('type', type).list()
        }

        entities.each { i ->
            result.add(modelMapper.toUserFederation(i, new MappingContext()))
        }
        result
    }

    @Override
    void delete(Long id) {
        UserFederationEntity entity = currentSession().get(UserFederationEntity, id)
        currentSession().delete(entity)
    }
}
