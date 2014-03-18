/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserLoginAttemptEntity;
import com.junbo.identity.spec.model.options.UserLoginAttemptGetOption;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserLoginAttemptDAO {
    UserLoginAttemptEntity save(UserLoginAttemptEntity entity);

    UserLoginAttemptEntity update(UserLoginAttemptEntity entity);

    UserLoginAttemptEntity get(@SeedParam Long id);

    List<UserLoginAttemptEntity> search(@SeedParam Long userId, UserLoginAttemptGetOption getOption);

    void delete(@SeedParam Long id);
}
