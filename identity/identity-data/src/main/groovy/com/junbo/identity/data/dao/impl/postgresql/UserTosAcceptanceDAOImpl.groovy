/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.common.id.UserTosId
import com.junbo.identity.data.dao.UserTosAcceptanceDAO
import com.junbo.identity.data.entity.user.UserTosAcceptanceEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.util.Constants
import com.junbo.identity.spec.model.user.UserTosAcceptance
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.IdGeneratorFacade
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
/**
 * Implementation for User Tos Acceptance DAO interface.
 */
class UserTosAcceptanceDAOImpl implements UserTosAcceptanceDAO {
    @Autowired
    private SessionFactory sessionFactory

    @Autowired
    private IdGeneratorFacade idGenerator

    @Autowired
    private ModelMapper modelMapper

    private Session currentSession() {
        sessionFactory.currentSession
    }

    @Override
    UserTosAcceptance save(UserTosAcceptance entity) {
        UserTosAcceptanceEntity userTosAcceptanceEntity = modelMapper.toUserTosAcceptance(entity, new MappingContext())
        userTosAcceptanceEntity.setId(idGenerator.nextId(UserTosId, userTosAcceptanceEntity.userId))
        userTosAcceptanceEntity.setCreatedBy(Constants.DEFAULT_CLIENT_ID)
        userTosAcceptanceEntity.setCreatedTime(new Date())
        currentSession().save(userTosAcceptanceEntity)
        get(userTosAcceptanceEntity.id)
    }

    @Override
    UserTosAcceptance update(UserTosAcceptance entity) {
        UserTosAcceptanceEntity userTosAcceptanceEntity = modelMapper.toUserTosAcceptance(entity, new MappingContext())
        UserTosAcceptanceEntity userTosAcceptanceEntityInDB =
                (UserTosAcceptanceEntity)currentSession().get(UserTosAcceptanceEntity, userTosAcceptanceEntity.id)
        currentSession().evict(userTosAcceptanceEntityInDB)

        userTosAcceptanceEntity.setCreatedBy(userTosAcceptanceEntityInDB.createdBy)
        userTosAcceptanceEntity.setCreatedTime(userTosAcceptanceEntityInDB.createdTime)
        userTosAcceptanceEntity.setUpdatedBy(Constants.DEFAULT_CLIENT_ID)
        userTosAcceptanceEntity.setUpdatedTime(new Date())
        currentSession().update(userTosAcceptanceEntity)
        currentSession().flush()

        return get(userTosAcceptanceEntity.id)
    }

    @Override
    UserTosAcceptance get(Long id) {
        modelMapper.toUserTosAcceptance(currentSession().get(UserTosAcceptanceEntity, id), new MappingContext())
    }

    @Override
    List<UserTosAcceptance> findByUserId(Long id, String tos) {
        def result = []
        List entities = null
        if (StringUtils.isEmpty(tos)) {
            entities = currentSession().
                    createSQLQuery('select * from user_tos_acceptance where user_id = :userId').
                    addEntity(UserTosAcceptanceEntity).setParameter('userId', id).list()
        }
        else {
            entities = currentSession().
              createSQLQuery('select * from user_tos_acceptance where user_id = :userId and tos_acceptance_url = :tos').
              addEntity(UserTosAcceptanceEntity).setParameter('userId', id).setParameter('tos', tos).list()
        }

        entities.each { i ->
            result.add(modelMapper.toUserTosAcceptance(i, new MappingContext()))
        }

        result
    }

    @Override
    void delete(Long id) {
        UserTosAcceptanceEntity entity = currentSession().get(UserTosAcceptanceEntity, id)
        currentSession().delete(entity)
    }
}
