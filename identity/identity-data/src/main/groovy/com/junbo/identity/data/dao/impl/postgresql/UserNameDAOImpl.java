/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserNameDAO;
import com.junbo.identity.data.entity.user.UserNameEntity;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/18/14.
 */
public class UserNameDAOImpl extends BaseDaoImpl<UserNameEntity, Long> implements UserNameDAO {

    @Override
    public void delete(@SeedParam Long id) {

    }

    @Override
    public UserNameEntity findByUserId(@SeedParam Long userId) {
        String query = "select * from user_name where user_id = " + userId;
        List results = currentSession().createSQLQuery(query).addEntity(UserNameEntity.class).list();
        if(results.size() == 0) {
            return null;
        } else {
            return (UserNameEntity)results.get(0);
        }
    }
}
