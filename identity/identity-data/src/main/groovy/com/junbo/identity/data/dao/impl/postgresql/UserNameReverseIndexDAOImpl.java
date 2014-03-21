/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserNameReverseIndexDAO;
import com.junbo.identity.data.entity.reverselookup.UserNameReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/18/14.
 */
public class UserNameReverseIndexDAOImpl extends ShardedDAOBase implements UserNameReverseIndexDAO {

    @Override
    public UserNameReverseIndexEntity save(UserNameReverseIndexEntity entity) {
        currentSession().save(entity);

        return get(entity.getUserName());
    }

    @Override
    public UserNameReverseIndexEntity update(UserNameReverseIndexEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getUserName());
    }

    @Override
    public UserNameReverseIndexEntity get(@SeedParam String userName) {
        String query = "select * from user_name_reverse_index where user_name = :userName";
        List<UserNameReverseIndexEntity> entities =
                currentSession().createSQLQuery(query).addEntity(UserNameReverseIndexEntity.class)
                        .setParameter("userName", userName).list();
        return entities.get(0);
    }

    @Override
    public void delete(@SeedParam String userName) {
        UserNameReverseIndexEntity entity = get(userName);
        currentSession().delete(entity);
    }
}
