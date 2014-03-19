/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.entity.user.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Implementation for User DAO..
 */
@Component

public class UserDAOImpl extends ShardedDAOBase implements UserDAO {
    @Override
    public UserEntity save(UserEntity user) {
        currentSession().save(user);

        return get(user.getId());
    }

    @Override
    public UserEntity update(UserEntity user) {
        currentSession().merge(user);
        currentSession().flush();

        return get(user.getId());
    }

    @Override
    public UserEntity get(Long userId) {
        return (UserEntity)currentSession().get(UserEntity.class, userId);
    }

    @Override
    public void delete(Long userId) {
        UserEntity entity = (UserEntity)currentSession().get(UserEntity.class, userId);
        currentSession().delete(entity);
    }
}
