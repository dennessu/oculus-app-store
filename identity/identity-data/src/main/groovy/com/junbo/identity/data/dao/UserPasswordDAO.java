/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserPasswordEntity;
import com.junbo.identity.spec.options.list.UserPasswordListOption;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface UserPasswordDAO {
    UserPasswordEntity save(UserPasswordEntity entity);

    UserPasswordEntity update(UserPasswordEntity entity);

    UserPasswordEntity get(@SeedParam Long id);

    List<UserPasswordEntity> search(@SeedParam Long userId, UserPasswordListOption getOption);

    void delete(@SeedParam Long id);
}
