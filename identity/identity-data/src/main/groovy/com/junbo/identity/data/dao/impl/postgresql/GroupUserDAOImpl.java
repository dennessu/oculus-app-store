/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.GroupUserDAO;
import com.junbo.identity.data.entity.group.GroupUserEntity;
import com.junbo.sharding.annotations.SeedParam;

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
        String query = "select * from group_user where group_id = " + groupId + " and user_id = " + userId;

        List list = currentSession().createSQLQuery(query).addEntity(GroupUserEntity.class).list();
        if(list != null) {
            return (GroupUserEntity)list.get(0);
        }
        return null;
    }

    @Override
    public List<GroupUserEntity> findByGroupId(@SeedParam Long groupId) {
        String query = "select * from group_user where group_id = " + groupId;

        List list = currentSession().createSQLQuery(query).addEntity(GroupUserEntity.class).list();
        return list;
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
