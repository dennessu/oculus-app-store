/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPasswordDAO;
import com.junbo.identity.data.entity.user.UserPasswordEntity;
import com.junbo.identity.spec.options.UserPasswordListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class UserPasswordDAOImpl extends BaseDaoImpl<UserPasswordEntity, Long> implements UserPasswordDAO {

    @Override
    public List<UserPasswordEntity> search(@SeedParam Long userId, UserPasswordListOptions getOption) {
        return null;
    }

    @Override
    public void delete(Long id) {
        UserPasswordEntity entity =
                (UserPasswordEntity)currentSession().get(UserPasswordEntity.class, id);
        currentSession().delete(entity);
    }
}
