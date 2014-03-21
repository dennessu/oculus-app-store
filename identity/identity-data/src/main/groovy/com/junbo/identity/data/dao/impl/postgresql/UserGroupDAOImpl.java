/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserGroupDAO;
import com.junbo.identity.data.entity.user.UserGroupEntity;
import com.junbo.identity.spec.options.list.UserGroupListOption;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserGroupDAOImpl extends ShardedDAOBase implements UserGroupDAO {
    @Override
    public UserGroupEntity save(UserGroupEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserGroupEntity update(UserGroupEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserGroupEntity get(Long id) {
        return (UserGroupEntity)currentSession().get(UserGroupEntity.class, id);
    }

    @Override
    public List<UserGroupEntity> search(Long userId, UserGroupListOption getOption) {
        String query = "select * from user_group where user_id = " + (getOption.getUserId().getValue()) +
                (getOption.getGroupId() == null ? "" : (" and group_id = " + getOption.getGroupId().getValue())) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserGroupEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        UserGroupEntity entity = (UserGroupEntity)currentSession().get(UserGroupEntity.class, id);
        currentSession().delete(entity);
    }
}
