/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserNameReverseLookupDAO;
import com.junbo.identity.data.entity.reverselookup.UserNameReverseLookupEntity;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/18/14.
 */
public class UserNameReverseLookupDAOImpl extends EntityDAOImpl implements UserNameReverseLookupDAO {

    @Override
    public UserNameReverseLookupEntity save(UserNameReverseLookupEntity entity) {
        currentSession().save(entity);

        return get(entity.getUserName());
    }

    @Override
    public UserNameReverseLookupEntity update(UserNameReverseLookupEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getUserName());
    }

    @Override
    public UserNameReverseLookupEntity get(@SeedParam String userName) {
        String query = "select * from user_name_reverse_lookup where user_name = :userName";
        List<UserNameReverseLookupEntity> entities =
                currentSession().createSQLQuery(query).addEntity(UserNameReverseLookupEntity.class)
                        .setParameter("userName", userName).list();
        return entities.get(0);
    }

    @Override
    public void delete(@SeedParam String userName) {
        UserNameReverseLookupEntity entity = get(userName);
        currentSession().delete(entity);
    }
}
