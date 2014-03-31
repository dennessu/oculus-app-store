/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql.index;

import com.junbo.identity.data.dao.impl.postgresql.ShardedDAOBase;
import com.junbo.identity.data.dao.index.UserAuthenticatorReverseIndexDAO;
import com.junbo.identity.data.entity.reverselookup.UserAuthenticatorReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/21/14.
 */
public class UserAuthenticatorReverseIndexDAOImpl extends ShardedDAOBase implements UserAuthenticatorReverseIndexDAO {

    @Override
    public UserAuthenticatorReverseIndexEntity save(UserAuthenticatorReverseIndexEntity entity) {
        currentSession().save(entity);
        return get(entity.getValue());
    }

    @Override
    public UserAuthenticatorReverseIndexEntity update(UserAuthenticatorReverseIndexEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getValue());
    }

    @Override
    public UserAuthenticatorReverseIndexEntity get(@SeedParam String value) {
        return (UserAuthenticatorReverseIndexEntity)currentSession().
                get(UserAuthenticatorReverseIndexEntity.class, value);
    }

    @Override
    public void delete(@SeedParam String value) {
        UserAuthenticatorReverseIndexEntity entity = get(value);
        currentSession().delete(entity);
    }
}
