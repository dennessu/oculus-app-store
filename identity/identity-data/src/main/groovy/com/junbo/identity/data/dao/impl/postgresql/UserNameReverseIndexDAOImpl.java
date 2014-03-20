/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserNameReverseIndexDAO;
import com.junbo.identity.data.entity.reverselookup.UserNameReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/18/14.
 */
public class UserNameReverseIndexDAOImpl implements UserNameReverseIndexDAO {

    @Override
    public UserNameReverseIndexEntity save(UserNameReverseIndexEntity entity) {
        return null;
    }

    @Override
    public UserNameReverseIndexEntity update(UserNameReverseIndexEntity entity) {
        return null;
    }

    @Override
    public UserNameReverseIndexEntity get(@SeedParam String userName) {
        return null;
    }

    @Override
    public void delete(@SeedParam String userName) {

    }
}
