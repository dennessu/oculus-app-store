/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserDAO
import com.junbo.identity.data.entity.user.UserEntity
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.hibernate.ShardScope
import com.junbo.sharding.view.ViewQueryFactory
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Implementation for User DAO..
 */
@CompileStatic
class UserDAOImpl implements UserDAO {

    private SessionFactory sessionFactory

    private ShardAlgorithm shardAlgorithm

    private ViewQueryFactory viewQueryFactory

    private IdGenerator idGenerator

    @Required
    void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setViewQueryFactory(ViewQueryFactory viewQueryFactory) {
        this.viewQueryFactory = viewQueryFactory
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    UserEntity save(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        def shardId = shardAlgorithm.shardId()

        user.id = idGenerator.nextIdByShardId(shardId)

        def currentSession = ShardScope.with(shardId) { sessionFactory.currentSession }

        currentSession.save(user)
        currentSession.flush()

        return get((Long) user.id)
    }

    @Override
    UserEntity update(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        def currentSession = ShardScope.with(shardAlgorithm.shardId(user.id)) { sessionFactory.currentSession }

        currentSession.merge(user)
        currentSession.flush()

        return get((Long) user.id)
    }

    @Override
    UserEntity get(Long userId) {
        def currentSession = ShardScope.with(shardAlgorithm.shardId(userId)) { sessionFactory.currentSession }

        return (UserEntity) currentSession.get(UserEntity, userId)
    }

    @Override
    void delete(Long userId) {
        def currentSession = ShardScope.with(shardAlgorithm.shardId(userId)) { sessionFactory.currentSession }

        UserEntity entity = (UserEntity) currentSession.get(UserEntity, userId)
        currentSession.delete(entity)
        currentSession.flush()
    }

    @Override
    // todo:    Liangfu:    This is temporary hack for sharding.
    // Due to internal call won't go through proxy
    Long getIdByCanonicalUsername(String username) {
        UserEntity example = new UserEntity()
        example.setUsername(username)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def userIds = viewQuery.list()

            return CollectionUtils.isEmpty(userIds) ? null : (Long) (userIds.get(0))
        }

        throw new RuntimeException()
    }
}
