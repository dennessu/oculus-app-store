/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl
import com.junbo.identity.data.dao.GroupUserDAO
import com.junbo.identity.data.entity.group.GroupUserEntity
import com.junbo.sharding.annotations.SeedParam
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions
/**
 * Created by liangfu on 3/21/14.
 */
@CompileStatic
class GroupUserDAOImpl  extends ShardedDAOBase implements GroupUserDAO {
    @Override
    GroupUserEntity create(GroupUserEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    GroupUserEntity update(GroupUserEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    GroupUserEntity findByGroupIdAndUserId(@SeedParam Long groupId, Long userId) {
        Criteria criteria = currentSession().createCriteria(GroupUserEntity)
        criteria.add(Restrictions.eq('groupId', groupId))
        criteria.add(Restrictions.eq('userId', userId))
        return criteria.list().size() == 0 ? null : (GroupUserEntity)criteria.list().get(0)
    }

    @Override
    List<GroupUserEntity> findByGroupId(@SeedParam Long groupId) {
        Criteria criteria = currentSession().createCriteria(GroupUserEntity)
        criteria.add(Restrictions.eq('groupId', groupId))
        return criteria.list()
    }

    @Override
    GroupUserEntity get(@SeedParam Long id) {
        return (GroupUserEntity)currentSession().get(GroupUserEntity, id)
    }

    @Override
    void delete(@SeedParam Long id) {
        GroupUserEntity entity = (GroupUserEntity)currentSession().get(GroupUserEntity, id)
        currentSession().delete(entity)
    }
}
