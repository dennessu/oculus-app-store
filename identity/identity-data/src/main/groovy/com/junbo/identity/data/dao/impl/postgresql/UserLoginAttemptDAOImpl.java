/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserLoginAttemptDAO;
import com.junbo.identity.data.entity.user.UserLoginAttemptEntity;
import com.junbo.identity.spec.options.LoginAttemptListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserLoginAttemptDAOImpl extends BaseDaoImpl<UserLoginAttemptEntity, Long> implements UserLoginAttemptDAO {

    @Override
    public List<UserLoginAttemptEntity> search(@SeedParam Long userId, LoginAttemptListOptions getOption) {
        return null;
    }

    @Override
    public void delete(@SeedParam Long id) {

    }
}
