/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.GroupUserDAO
import com.junbo.identity.data.entity.group.GroupUserEntity
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 3/21/14.
 */
@CompileStatic
class GroupUserDAOImpl  extends BaseDAO implements GroupUserDAO {
    @Override
    GroupUserEntity create(GroupUserEntity entity) {
        entity.id = idGenerator.nextId(entity.groupId)

        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    GroupUserEntity update(GroupUserEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    GroupUserEntity findByGroupIdAndUserId(Long groupId, Long userId) {
        Criteria criteria = currentSession(groupId).createCriteria(GroupUserEntity)
        criteria.add(Restrictions.eq('groupId', groupId))
        criteria.add(Restrictions.eq('userId', userId))
        return criteria.list().size() == 0 ? null : (GroupUserEntity)criteria.list().get(0)
    }

    @Override
    List<GroupUserEntity> findByGroupId(Long groupId) {
        Criteria criteria = currentSession(groupId).createCriteria(GroupUserEntity)
        criteria.add(Restrictions.eq('groupId', groupId))
        return criteria.list()
    }

    @Override
    GroupUserEntity get(Long id) {
        return (GroupUserEntity)currentSession(id).get(GroupUserEntity, id)
    }

    @Override
    void delete(Long id) {
        GroupUserEntity entity = (GroupUserEntity)currentSession(id).get(GroupUserEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }
}
