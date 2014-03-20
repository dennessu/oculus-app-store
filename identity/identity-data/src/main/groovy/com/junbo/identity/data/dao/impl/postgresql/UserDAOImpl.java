/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.entity.user.UserEntity;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

/**
 * Implementation for User DAO..
 */
@Component
public class UserDAOImpl extends BaseDaoImpl<UserEntity, Long> implements UserDAO {

    @Override
    public void delete(@SeedParam Long userId) {

    }
}
