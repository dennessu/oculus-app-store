/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserProfileDAO
import com.junbo.identity.data.entity.user.UserProfileEntity
import com.junbo.identity.data.entity.user.UserProfileType
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.util.Constants
import com.junbo.identity.spec.model.user.UserProfile
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.IdGenerator
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
/**
 * Implementation for UserProfileDAO interface
 */
class UserProfileDAOImpl implements UserProfileDAO {
    @Autowired
    private SessionFactory sessionFactory

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    private IdGenerator idGenerator

    private Session currentSession() {
        sessionFactory.currentSession
    }

    @Override
    UserProfile save(UserProfile entity) {
        UserProfileEntity userProfileEntity = modelMapper.toUserProfileEntity(entity, new MappingContext())
        userProfileEntity.setId(idGenerator.nextId(entity.userId.value))
        userProfileEntity.setCreatedBy(Constants.DEFAULT_CLIENT_ID)
        userProfileEntity.setCreatedTime(new Date())
        currentSession().save(userProfileEntity)
        get(userProfileEntity.id)
    }

    @Override
    UserProfile update(UserProfile entity) {
        UserProfileEntity userProfileEntity = modelMapper.toUserProfileEntity(entity, new MappingContext())
        UserProfileEntity userProfileEntityInDB = currentSession().get(UserProfileEntity, userProfileEntity.id)
        currentSession().evict(userProfileEntityInDB)

        userProfileEntity.setCreatedBy(userProfileEntityInDB.createdBy)
        userProfileEntity.setCreatedTime(userProfileEntityInDB.createdTime)
        userProfileEntity.setUpdatedBy(Constants.DEFAULT_CLIENT_ID)
        userProfileEntity.setUpdatedTime(new Date())
        currentSession().update(userProfileEntity)
        currentSession().flush()

        return get(userProfileEntity.id)
    }

    @Override
    UserProfile get(Long id) {
        UserProfileEntity userProfileEntity = currentSession().get(UserProfileEntity, id)
        modelMapper.toUserProfile(userProfileEntity, new MappingContext())
    }

    @Override
    List<UserProfile> findByUser(Long userId, String type) {
        def result = []
        List userProfiles = null
        if (StringUtils.isEmpty(type)) {
            userProfiles = currentSession().
                    createSQLQuery('select * from user_profile where user_id = :userId').
                    addEntity(UserProfileEntity).setParameter('userId', userId).list()
        }
        else {
            UserProfileType userProfileType = UserProfileType.valueOf(type)
            userProfiles = currentSession().
                    createSQLQuery('select * from user_profile where user_id = :userId and type = :type').
                    addEntity(UserProfileEntity).setParameter('userId', userId).setParameter('type', userProfileType.id)
                    .list()
        }

        userProfiles.each { i ->
            result.add(modelMapper.toUserProfile(i, new MappingContext()))
        }

        result
    }

    @Override
    void delete(Long id) {
        currentSession().delete(get(id))
    }
}
