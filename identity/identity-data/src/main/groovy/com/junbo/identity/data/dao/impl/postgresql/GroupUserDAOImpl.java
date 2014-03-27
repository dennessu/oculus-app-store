/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.GroupUserDAO;
import com.junbo.identity.data.entity.group.GroupUserEntity;
import com.junbo.sharding.annotations.SeedParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by liangfu on 3/21/14.
 */
public class GroupUserDAOImpl  extends ShardedDAOBase implements GroupUserDAO {
    @Override
    public GroupUserEntity save(GroupUserEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public GroupUserEntity update(GroupUserEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public GroupUserEntity findByGroupIdAndUserId(@SeedParam Long groupId, Long userId) {
        Criteria criteria = currentSession().createCriteria(GroupUserEntity.class);
        criteria.add(Restrictions.eq("groupId", groupId));
        criteria.add(Restrictions.eq("userId", userId));
        return criteria.list().size() == 0 ? null : (GroupUserEntity)criteria.list().get(0);
    }

    @Override
    public List<GroupUserEntity> findByGroupId(@SeedParam Long groupId) {
        Criteria criteria = currentSession().createCriteria(GroupUserEntity.class);
        criteria.add(Restrictions.eq("groupId", groupId));
        return criteria.list();
    }

    @Override
    public GroupUserEntity get(@SeedParam Long id) {
        return (GroupUserEntity)currentSession().get(GroupUserEntity.class, id);
    }

    @Override
    public void delete(@SeedParam Long id) {
        GroupUserEntity entity = (GroupUserEntity)currentSession().get(GroupUserEntity.class, id);
        currentSession().delete(entity);
    }
}
