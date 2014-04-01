/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.index;

import com.junbo.identity.data.dao.impl.ShardedDAOBase;
import com.junbo.identity.data.dao.index.UserEmailReverseIndexDAO;
import com.junbo.identity.data.entity.reverselookup.UserEmailReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/21/14.
 */
public class UserEmailReverseIndexDAOImpl extends ShardedDAOBase implements UserEmailReverseIndexDAO {
    @Override
    public UserEmailReverseIndexEntity save(UserEmailReverseIndexEntity entity) {
        currentSession().save(entity);
        return get(entity.getValue());
    }

    @Override
    public UserEmailReverseIndexEntity update(UserEmailReverseIndexEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getValue());
    }

    @Override
    public UserEmailReverseIndexEntity get(@SeedParam String userEmail) {
        return (UserEmailReverseIndexEntity)currentSession().
                get(UserEmailReverseIndexEntity.class, userEmail);
    }

    @Override
    public void delete(@SeedParam String userEmail) {
        UserEmailReverseIndexEntity entity = get(userEmail);
        currentSession().delete(entity);
    }
}
